package com.nexora.dto.response;

import com.nexora.dto.response.UserResponse;
import com.nexora.model.enums.CustomerOrigin;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String phone,
        String email,
        CustomerOrigin origin,
        UserResponse user  // nullable — só vem se o customer tiver conta
) {}