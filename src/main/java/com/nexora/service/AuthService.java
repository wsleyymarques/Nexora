package com.nexora.service;

import com.nexora.dto.request.LoginRequest;
import com.nexora.dto.request.RegisterRequest;
import com.nexora.dto.response.AuthResponse;
import com.nexora.dto.response.UserResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.User;
import com.nexora.model.enums.UserOrigin;
import com.nexora.repository.UserRepository;
import com.nexora.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already in use", HttpStatus.CONFLICT);
        }

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .origin(UserOrigin.DIRECT)
                .build();

        userRepository.save(user);

        var token = generateToken(user);
        return new AuthResponse(token, toResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        return new AuthResponse(generateToken(user), toResponse(user));
    }

    private String generateToken(User user) {
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
        return jwtService.generateToken(userDetails);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone());
    }
}
