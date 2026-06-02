package com.nexora.dto.response;

import com.nexora.model.enums.ProductType;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID storeId,
        String name,
        String description,
        BigDecimal price,
        ProductType type,
        boolean active
) {}
