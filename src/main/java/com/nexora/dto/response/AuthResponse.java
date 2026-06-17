package com.nexora.dto.response;

import com.nexora.dto.response.UserResponse;

public record AuthResponse(
        String token,
        String type,
        UserResponse user
) {}