package com.nexora.dto.request;

public record ResetPasswordRequest(
        String email,
        String code,
        String newPassword
) {
}