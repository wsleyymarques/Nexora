package com.nexora.dto.response;

import com.nexora.model.enums.UserOrigin;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String phone,
        String email,
        UserOrigin origin
) {}