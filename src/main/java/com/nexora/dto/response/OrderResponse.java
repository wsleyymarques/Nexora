package com.nexora.dto.response;

import com.nexora.model.enums.OrderChannel;
import com.nexora.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID storeId,
        UUID customerId,
        String customerName,
        OrderChannel channel,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {}