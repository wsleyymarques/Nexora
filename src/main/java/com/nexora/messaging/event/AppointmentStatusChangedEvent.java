package com.nexora.messaging.event;

import com.nexora.model.enums.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AppointmentStatusChangedEvent(
        UUID appointmentId,
        UUID customerId,
        String customerPhone,
        String customerEmail,
        String productName,
        LocalDateTime scheduledAt,
        AppointmentStatus oldStatus,
        AppointmentStatus newStatus
) {}