package com.nexora.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RequestTrackingFilter extends OncePerRequestFilter {

    private final CurrentRequest currentRequest;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        currentRequest.setIp(request.getRemoteAddr());

        currentRequest.setOrigin(
                request.getHeader("Origin")
        );

        currentRequest.setReferer(
                request.getHeader("Referer")
        );

        currentRequest.setUserAgent(
                request.getHeader("User-Agent")
        );

        currentRequest.setPath(
                request.getRequestURI()
        );

        currentRequest.setMethod(
                request.getMethod()
        );

        filterChain.doFilter(request, response);
    }
}