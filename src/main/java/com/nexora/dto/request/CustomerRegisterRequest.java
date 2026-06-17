package com.nexora.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRegisterRequest(
        @NotBlank String name,        // sem private
        @NotBlank String phone,
        @Email String email,
        @Size(min = 8) String password
) {}
