package com.nexora.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OtpValidateRequest(
        @NotBlank String phone,
        @NotBlank String code
) {}