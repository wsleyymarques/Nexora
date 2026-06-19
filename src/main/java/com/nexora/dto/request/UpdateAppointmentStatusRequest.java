package com.nexora.dto.request;

import com.nexora.model.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAppointmentStatusRequest(
        @NotNull AppointmentStatus status,
        String note
) {}