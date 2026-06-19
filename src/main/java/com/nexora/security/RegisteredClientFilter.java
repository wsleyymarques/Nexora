package com.nexora.security;

import com.nexora.exception.BusinessException;
import com.nexora.model.entity.RegisteredClient;
import com.nexora.service.RegisteredClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.cors.CorsUtils;

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

        if (CorsUtils.isPreFlightRequest(request)
                || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = request.getHeader("X-CLIENT-KEY");

        if (clientKey == null || clientKey.isBlank()) {
            throw new BusinessException(
                    "Client key required",
                    HttpStatus.UNAUTHORIZED
            );
        }

        RegisteredClient client = registeredClientService.validate(clientKey);

        validateOrigin(request, client);
        validateIp(request, client);

        currentRequest.setRegisteredClientId(
                client.getId()
        );

        registeredClientService.touch(client);

        filterChain.doFilter(request, response);
    }

    private void validateOrigin(HttpServletRequest request, RegisteredClient client) {
        String requestOrigin = request.getHeader("Origin");
        String allowedOrigin = client.getAllowedOrigin();

        if (requestOrigin == null || requestOrigin.isBlank()
                || allowedOrigin == null || allowedOrigin.isBlank()) {
            return;
        }

        if (!allowedOrigin.equals(requestOrigin)) {
            throw new BusinessException(
                    "Origin not allowed for this client",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private void validateIp(HttpServletRequest request, RegisteredClient client) {
        String allowedIp = client.getAllowedIp();

        if (allowedIp == null || allowedIp.isBlank()) {
            return;
        }

        String requestIp = request.getRemoteAddr();

        if (!allowedIp.equals(requestIp)) {
            throw new BusinessException(
                    "IP not allowed for this client",
                    HttpStatus.FORBIDDEN
            );
        }
    }
}
