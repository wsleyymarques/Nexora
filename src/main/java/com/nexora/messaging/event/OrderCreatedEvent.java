package com.nexora.messaging.event;

import com.nexora.model.enums.OrderChannel;
import com.nexora.model.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record OrderCreatedEvent(
        UUID orderId,
        UUID storeId,
        UUID customerId,
        String customerName,
        String customerPhone,
        OrderChannel channel,
        OrderStatus status,
        BigDecimal total,
        LocalDateTime createdAt
) {}