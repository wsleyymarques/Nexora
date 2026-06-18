package com.nexora.dto.request;

import com.nexora.model.enums.StoreRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InviteStoreMemberRequest(
        @NotNull UUID userId,
        @NotNull StoreRole role
) {}