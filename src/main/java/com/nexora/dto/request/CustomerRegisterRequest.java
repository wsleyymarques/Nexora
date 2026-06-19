package com.nexora.dto.request;

import com.nexora.model.enums.CustomerOrigin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CustomerRegisterRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @Email String email,
        @Size(min = 8) String password,
        @NotNull UUID storeId,
        CustomerOrigin origin
) {}
