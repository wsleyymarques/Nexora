package com.nexora.dto.request;

import com.nexora.model.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateResourceRequest(

        @NotBlank
        String name,

        @NotNull
        ResourceType type,

        UUID storeMemberId

) {}