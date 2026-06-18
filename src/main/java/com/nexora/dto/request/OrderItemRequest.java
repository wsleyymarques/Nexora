package com.nexora.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderItemRequest(
        @NotNull UUID productId,
        @Positive int quantity,
        Integer estimatedMinutes,
        LocalDateTime scheduledAt
) {}