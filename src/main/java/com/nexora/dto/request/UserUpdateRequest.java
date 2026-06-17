package com.nexora.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        String name,
        String phone,
        @Email String email,
        @Size(min = 8) String newPassword,
        String currentPassword
) {}