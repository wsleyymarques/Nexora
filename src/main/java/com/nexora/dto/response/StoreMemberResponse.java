package com.nexora.dto.response;

import com.nexora.model.enums.StoreRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record StoreMemberResponse(
        UUID id,
        UUID userId,
        String userName,
        String userEmail,
        String userAvatarUrl,
        StoreRole role,
        LocalDateTime joinedAt
) {}