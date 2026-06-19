package com.nexora.dto.response;

import com.nexora.model.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentDetailResponse(
        UUID id,
        UUID orderId,
        UUID customerId,
        String customerName,
        String customerPhone,
        UUID resourceId,
        String resourceName,
        LocalDateTime scheduledAt,
        int durationMinutes,
        AppointmentStatus status,
        LocalDateTime createdAt
) {}