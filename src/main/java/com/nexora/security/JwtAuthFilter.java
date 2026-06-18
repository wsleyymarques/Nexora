package com.nexora.security;

import com.nexora.exception.BusinessException;
import com.nexora.model.entity.User;
import com.nexora.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CurrentRequest currentRequest;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final String subject = jwtService.extractSubject(token);

            if (subject != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepository.findByEmail(subject)
                        .or(() -> userRepository.findByPhone(subject))
                        .orElseThrow(() -> new BusinessException(
                                "User not found",
                                HttpStatus.UNAUTHORIZED
                        ));

                var userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(subject)
                        .password(user.getPassword() != null ? user.getPassword() : "")
                        .roles("USER")
                        .build();

                if (jwtService.isValid(token, userDetails)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    currentRequest.setUser(user);
                    currentRequest.setUserId(user.getId());
                }
            }
        } catch (JwtException | IllegalArgumentException | BusinessException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
