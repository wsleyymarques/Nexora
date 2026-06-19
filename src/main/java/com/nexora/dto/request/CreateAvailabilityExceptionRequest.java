package com.nexora.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAvailabilityExceptionRequest(
        @NotNull LocalDate date,
        LocalTime startTime,    // null = dia bloqueado
        LocalTime endTime,      // null = dia bloqueado
        boolean available
) {}