package com.nexora.service;

import com.nexora.dto.request.*;
import com.nexora.dto.response.AuthResponse;
import com.nexora.dto.response.UserResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.User;
import com.nexora.model.enums.OtpType;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Transactional
    public AuthResponse register(StoreRegisterRequest request) {
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
        return new AuthResponse(token, "Bearer", toResponse(user));
    }

    @Transactional
    public void registerCustomer(CustomerRegisterRequest request) {

        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already in use", HttpStatus.CONFLICT);
        }

        var user = User.builder()
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .password(request.password() != null
                        ? passwordEncoder.encode(request.password())
                        : null)
                .origin(UserOrigin.DIRECT)
                .verified(false)
                .build();

        userRepository.save(user);
        otpService.sendToPhone(
                request.phone(),
                OtpType.PHONE_VERIFICATION
        );
    }

    @Transactional
    public AuthResponse verifyOtp(OtpValidateRequest request) {

        otpService.validate(request.phone(), request.code(), OtpType.PHONE_VERIFICATION);

        var user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        user.setVerified(true);
        userRepository.save(user);

        var token = generateToken(user);
        return new AuthResponse(token, "Bearer", toResponse(user));
    }

    public AuthResponse login(StoreLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        return new AuthResponse(generateToken(user),"Bearer", toResponse(user));
    }

    @Transactional
    public AuthResponse loginCustomer(CustomerLoginRequest request) {

        if (request.phone() == null && request.email() == null) {
            throw new BusinessException("Informe telefone ou email", HttpStatus.BAD_REQUEST);
        }

        if (request.phone() != null && request.email() == null) {
            var user = userRepository.findByPhone(request.phone())
                    .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

            if (!user.isVerified()) {
                throw new BusinessException("Phone not verified", HttpStatus.UNAUTHORIZED);
            }

            otpService.sendToPhone(request.phone(), OtpType.PHONE_VERIFICATION);
            throw new BusinessException("OTP sent", HttpStatus.ACCEPTED);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (!user.isVerified()) {
            throw new BusinessException("Account not verified", HttpStatus.UNAUTHORIZED);
        }

        return new AuthResponse(generateToken(user), "Bearer", toResponse(user));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BusinessException(
                                "User not found",
                                HttpStatus.NOT_FOUND
                        ));

        otpService.validate(
                request.email(),
                request.code(),
                OtpType.PASSWORD_RESET
        );

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BusinessException(
                                "User not found",
                                HttpStatus.NOT_FOUND
                        ));

        otpService.sendToEmail(
                user.getEmail(),
                OtpType.PASSWORD_RESET
        );
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(
                                "User not found",
                                HttpStatus.NOT_FOUND
                        ));

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        if (request.email() != null) {

            if (!request.email().equals(user.getEmail())
                    && userRepository.existsByEmail(request.email())) {

                throw new BusinessException(
                        "Email already in use",
                        HttpStatus.CONFLICT
                );
            }

            user.setEmail(request.email());
        }

        if (request.newPassword() != null) {

            // usuário já possui senha
            if (user.getPassword() != null) {

                if (request.currentPassword() == null) {
                    throw new BusinessException(
                            "Current password is required",
                            HttpStatus.BAD_REQUEST
                    );
                }

                if (!passwordEncoder.matches(
                        request.currentPassword(),
                        user.getPassword())) {

                    throw new BusinessException(
                            "Current password is incorrect",
                            HttpStatus.BAD_REQUEST
                    );
                }
            }

            user.setPassword(
                    passwordEncoder.encode(request.newPassword())
            );
        }

        userRepository.save(user);

        return toResponse(user);
    }

    private String generateToken(User user) {
        String username = user.getEmail() != null ? user.getEmail() : user.getPhone();

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword() != null ? user.getPassword() : "")
                .roles("USER")
                .build();
        return jwtService.generateToken(userDetails);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getOrigin()
        );
    }
}
