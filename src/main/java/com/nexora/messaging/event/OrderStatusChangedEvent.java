package com.nexora.messaging.event;

import com.nexora.model.enums.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderStatusChangedEvent(
        UUID orderId,
        UUID customerId,
        String customerPhone,
        String customerEmail,
        OrderStatus oldStatus,
        OrderStatus newStatus
) {}