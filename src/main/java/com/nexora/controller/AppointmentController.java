package com.nexora.controller;

import com.nexora.audit.Auditable;
import com.nexora.dto.request.UpdateAppointmentStatusRequest;
import com.nexora.dto.response.AppointmentDetailResponse;
import com.nexora.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/api/v1/stores/{storeId}/appointments")
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<List<AppointmentDetailResponse>> findByStore(
            @PathVariable UUID storeId) {
        return ResponseEntity.ok(appointmentService.findByStore(storeId));
    }

    @GetMapping("/api/v1/stores/{storeId}/appointments/{appointmentId}")
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<AppointmentDetailResponse> findById(
            @PathVariable UUID storeId,
            @PathVariable UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.findById(storeId, appointmentId));
    }

    @PatchMapping("/api/v1/stores/{storeId}/appointments/{appointmentId}/status")
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    @Auditable(action = "APPOINTMENT_STATUS_UPDATED", entityType = "APPOINTMENT")
    public ResponseEntity<AppointmentDetailResponse> updateStatus(
            @PathVariable UUID storeId,
            @PathVariable UUID appointmentId,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {
        return ResponseEntity.ok(
                appointmentService.updateStatus(storeId, appointmentId, request));
    }

    @GetMapping("/api/v1/appointments/me")
    public ResponseEntity<List<AppointmentDetailResponse>> myAppointments() {
        return ResponseEntity.ok(appointmentService.findMyAppointments());
    }
}