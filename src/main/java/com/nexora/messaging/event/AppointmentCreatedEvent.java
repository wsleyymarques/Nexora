package com.nexora.messaging.event;

import com.nexora.model.enums.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AppointmentCreatedEvent(
        UUID appointmentId,
        UUID orderId,
        UUID customerId,
        String customerName,
        String customerPhone,
        String productName,
        LocalDateTime scheduledAt,
        int durationMinutes,
        AppointmentStatus status
) {}