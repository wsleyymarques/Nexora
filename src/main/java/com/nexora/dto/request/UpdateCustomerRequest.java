package com.nexora.dto.request;

public record UpdateCustomerRequest(
        String name,
        String phone,
        String email
) {}