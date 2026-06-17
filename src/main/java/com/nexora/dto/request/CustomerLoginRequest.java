package com.nexora.dto.request;

public record CustomerLoginRequest(
        String phone,
        String email,
        String password
) {}