package com.nexora.service;

import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Resource;
import com.nexora.model.entity.ResourceAvailability;
import com.nexora.model.entity.ResourceAvailabilityException;
import com.nexora.model.enums.ResourceType;
import com.nexora.repository.AppointmentRepository;
import com.nexora.repository.ResourceAvailabilityExceptionRepository;
import com.nexora.repository.ResourceAvailabilityRepository;
import com.nexora.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ResourceRepository resourceRepository;
    private final ResourceAvailabilityRepository availabilityRepository;
    private final ResourceAvailabilityExceptionRepository exceptionRepository;
    private final AppointmentRepository appointmentRepository;

    public Resource findAvailableResource(UUID storeId, ResourceType type,
                                          LocalDateTime scheduledAt, int durationMinutes) {

        List<Resource> resources = resourceRepository
                .findByStoreIdAndTypeAndActiveTrue(storeId, type);

        if (resources.isEmpty()) {
            throw new BusinessException(
                    "Nenhum recurso do tipo " + type + " encontrado na loja", HttpStatus.NOT_FOUND);
        }

        LocalDateTime end = scheduledAt.plusMinutes(durationMinutes);

        return resources.stream()
                .filter(r -> isAvailable(r, scheduledAt, end))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Nenhum recurso disponível para o horário solicitado", HttpStatus.CONFLICT));
    }

    public boolean isAvailable(Resource resource, LocalDateTime start, LocalDateTime end) {
        LocalDate date = start.toLocalDate();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        if (!isWithinSchedule(resource, date, startTime, endTime)) {
            return false;
        }

        List conflicts = appointmentRepository.findConflicting(
                resource.getId(), start, end);

        return conflicts.isEmpty();
    }

    private boolean isWithinSchedule(Resource resource, LocalDate date,
                                     LocalTime startTime, LocalTime endTime) {

        Optional<ResourceAvailabilityException> exception =
                exceptionRepository.findByResourceIdAndDate(resource.getId(), date);

        if (exception.isPresent()) {
            ResourceAvailabilityException ex = exception.get();

            if (!ex.isAvailable()) {
                return false;
            }

            return !startTime.isBefore(ex.getStartTime()) &&
                    !endTime.isAfter(ex.getEndTime());
        }

        Optional<ResourceAvailability> availability =
                availabilityRepository.findByResourceIdAndDayOfWeekAndActiveTrue(
                        resource.getId(), date.getDayOfWeek());

        if (availability.isEmpty()) {
            return false;
        }

        ResourceAvailability avail = availability.get();
        return !startTime.isBefore(avail.getStartTime()) &&
                !endTime.isAfter(avail.getEndTime());
    }

    public List<LocalDateTime> findAvailableSlots(UUID storeId, ResourceType type,
                                                  LocalDate date, int durationMinutes) {

        List<Resource> resources = resourceRepository
                .findByStoreIdAndTypeAndActiveTrue(storeId, type);

        return resources.stream()
                .flatMap(r -> getSlotsForResource(r, date, durationMinutes).stream())
                .distinct()
                .sorted()
                .toList();
    }

    private List<LocalDateTime> getSlotsForResource(Resource resource,
                                                    LocalDate date, int durationMinutes) {

        LocalDate d = date;
        LocalTime startTime;
        LocalTime endTime;

        Optional<ResourceAvailabilityException> exception =
                exceptionRepository.findByResourceIdAndDate(resource.getId(), d);

        if (exception.isPresent()) {
            ResourceAvailabilityException ex = exception.get();
            if (!ex.isAvailable()) return List.of();
            startTime = ex.getStartTime();
            endTime = ex.getEndTime();
        } else {
            Optional<ResourceAvailability> availability =
                    availabilityRepository.findByResourceIdAndDayOfWeekAndActiveTrue(
                            resource.getId(), d.getDayOfWeek());
            if (availability.isEmpty()) return List.of();
            startTime = availability.get().getStartTime();
            endTime = availability.get().getEndTime();
        }

        return generateSlots(resource, date, startTime, endTime, durationMinutes);
    }

    private List<LocalDateTime> generateSlots(Resource resource, LocalDate date,
                                              LocalTime startTime, LocalTime endTime,
                                              int durationMinutes) {

        java.util.List<LocalDateTime> slots = new java.util.ArrayList<>();
        LocalTime cursor = startTime;

        while (!cursor.plusMinutes(durationMinutes).isAfter(endTime)) {
            LocalDateTime slotStart = LocalDateTime.of(date, cursor);
            LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);

            List conflicts = appointmentRepository.findConflicting(
                    resource.getId(), slotStart, slotEnd);

            if (conflicts.isEmpty()) {
                slots.add(slotStart);
            }

            cursor = cursor.plusMinutes(durationMinutes);
        }

        return slots;
    }
}