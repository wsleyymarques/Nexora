package com.nexora.dto.response;

import com.nexora.model.enums.ProductType;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        Integer estimatedMinutes,
        AppointmentResponse appointment
) {}