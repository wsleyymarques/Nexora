package com.nexora.dto.response;

import com.nexora.model.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID resourceId,
        String resourceName,
        LocalDateTime scheduledAt,
        int durationMinutes,
        AppointmentStatus status
) {}