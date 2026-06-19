package com.nexora.dto.request;

import com.nexora.model.enums.CustomerOrigin;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateCustomerRequest(
        @NotBlank String name,
        @NotBlank String phone,
        String email,
        CustomerOrigin origin,
        UUID userId
) {}