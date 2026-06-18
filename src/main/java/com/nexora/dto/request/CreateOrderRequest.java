package com.nexora.dto.request;

import com.nexora.model.enums.OrderChannel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID storeId,
        @NotNull UUID customerId,
        @NotNull OrderChannel channel,
        @NotEmpty List<OrderItemRequest> items
) {}