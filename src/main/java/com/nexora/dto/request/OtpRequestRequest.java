package com.nexora.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OtpRequestRequest(
        @NotBlank String phone
) {}