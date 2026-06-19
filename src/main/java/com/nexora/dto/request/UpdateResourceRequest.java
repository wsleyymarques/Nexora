package com.nexora.dto.request;

import java.util.UUID;

public record UpdateResourceRequest(

        String name,

        UUID storeMemberId

) {}