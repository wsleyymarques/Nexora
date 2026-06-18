package com.nexora.security;

import com.nexora.exception.BusinessException;
import com.nexora.service.RegisteredClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RegisteredClientFilter extends OncePerRequestFilter {

    private final RegisteredClientService registeredClientService;
    private final CurrentRequest currentRequest;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientKey = request.getHeader("X-CLIENT-KEY");

        if (clientKey == null || clientKey.isBlank()) {
            throw new BusinessException(
                    "Client key required",
                    HttpStatus.UNAUTHORIZED
            );
        }

        var client = registeredClientService.validate(clientKey);

        currentRequest.setRegisteredClientId(
                client.getId()
        );

        filterChain.doFilter(request, response);
    }
}