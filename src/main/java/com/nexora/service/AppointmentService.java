package com.nexora.service;

import com.nexora.dto.request.UpdateAppointmentStatusRequest;
import com.nexora.dto.response.AppointmentDetailResponse;
import com.nexora.exception.BusinessException;
import com.nexora.messaging.event.AppointmentStatusChangedEvent;
import com.nexora.messaging.producer.MessageProducer;
import com.nexora.model.entity.Appointment;
import com.nexora.model.enums.AppointmentStatus;
import com.nexora.repository.AppointmentRepository;
import com.nexora.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SecurityUtils securityUtils;
    private final MessageProducer messageProducer;

    @Transactional(readOnly = true)
    public List<AppointmentDetailResponse> findByStore(UUID storeId) {
        return appointmentRepository.findByStoreId(storeId)
                .stream()
                .map(this::toDetailResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentDetailResponse findById(UUID storeId, UUID appointmentId) {
        Appointment appointment = findAppointmentInStore(storeId, appointmentId);
        return toDetailResponse(appointment);
    }

    @Transactional
    public AppointmentDetailResponse updateStatus(UUID storeId, UUID appointmentId,
                                                  UpdateAppointmentStatusRequest request) {

        Appointment appointment = findAppointmentInStore(storeId, appointmentId);

        validateStatusTransition(appointment.getStatus(), request.status());

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(request.status());
        appointmentRepository.save(appointment);

        messageProducer.publishAppointmentStatusChanged(
                AppointmentStatusChangedEvent.builder()
                        .appointmentId(appointment.getId())
                        .customerId(appointment.getCustomer().getId())
                        .customerPhone(appointment.getCustomer().getPhone())
                        .customerEmail(appointment.getCustomer().getEmail())
                        .productName(appointment.getOrderItem().getProduct().getName())
                        .scheduledAt(appointment.getScheduledAt())
                        .oldStatus(oldStatus)
                        .newStatus(request.status())
                        .build()
        );

        return toDetailResponse(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDetailResponse> findMyAppointments() {
        var currentUser = securityUtils.getCurrentUser();

        return appointmentRepository
                .findByCustomerUserIdOrderByScheduledAtDesc(currentUser.getId())
                .stream()
                .map(this::toDetailResponse)
                .toList();
    }

    private void validateStatusTransition(AppointmentStatus current,
                                          AppointmentStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == AppointmentStatus.CONFIRMED
                    || next == AppointmentStatus.CANCELLED;
            case CONFIRMED -> next == AppointmentStatus.DONE
                    || next == AppointmentStatus.CANCELLED;
            case DONE, CANCELLED -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "Invalid status transition: " + current + " → " + next,
                    HttpStatus.BAD_REQUEST);
        }
    }

    private Appointment findAppointmentInStore(UUID storeId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(
                        "Appointment not found", HttpStatus.NOT_FOUND));

        if (!appointment.getOrderItem().getOrder().getStore().getId().equals(storeId)) {
            throw new BusinessException(
                    "Appointment does not belong to this store",
                    HttpStatus.BAD_REQUEST);
        }

        return appointment;
    }

    private AppointmentDetailResponse toDetailResponse(Appointment a) {
        return new AppointmentDetailResponse(
                a.getId(),
                a.getOrderItem().getOrder().getId(),
                a.getCustomer().getId(),
                a.getCustomer().getName(),
                a.getCustomer().getPhone(),
                a.getResource().getId(),
                a.getResource().getName(),
                a.getScheduledAt(),
                a.getDurationMinutes(),
                a.getStatus(),
                a.getCreatedAt()
        );
    }
}