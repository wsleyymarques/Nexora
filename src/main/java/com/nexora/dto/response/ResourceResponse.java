package com.nexora.dto.response;

import com.nexora.model.enums.ResourceType;

import java.util.UUID;

public record ResourceResponse(

        UUID id,

        String name,

        ResourceType type,

        boolean active,

        UUID storeMemberId

) {}