package com.nexora.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record StoreLoginRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {}
