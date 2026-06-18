package com.nexora.security;

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
public class RateLimitFilter extends OncePerRequestFilter {

    private final IpRateLimitService ipRateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        if (!ipRateLimitService.allowRequest(ip)) {

            response.setStatus(
                    HttpStatus.TOO_MANY_REQUESTS.value()
            );

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                      "message":"Too many requests"
                    }
                    """);

            return;
        }

        filterChain.doFilter(request, response);
    }
}