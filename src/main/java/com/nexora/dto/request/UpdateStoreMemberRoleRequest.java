package com.nexora.dto.request;

import com.nexora.model.enums.StoreRole;
import jakarta.validation.constraints.NotNull;

public record UpdateStoreMemberRoleRequest(
        @NotNull StoreRole role
) {}