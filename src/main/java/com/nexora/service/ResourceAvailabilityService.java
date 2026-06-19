package com.nexora.service;

import com.nexora.dto.request.CreateAvailabilityExceptionRequest;
import com.nexora.dto.request.CreateAvailabilityRequest;
import com.nexora.dto.response.AvailabilityExceptionResponse;
import com.nexora.dto.response.AvailabilityResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Resource;
import com.nexora.model.entity.ResourceAvailability;
import com.nexora.model.entity.ResourceAvailabilityException;
import com.nexora.repository.ResourceAvailabilityExceptionRepository;
import com.nexora.repository.ResourceAvailabilityRepository;
import com.nexora.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceAvailabilityService {

    private final ResourceRepository resourceRepository;
    private final ResourceAvailabilityRepository availabilityRepository;
    private final ResourceAvailabilityExceptionRepository exceptionRepository;
    private final AvailabilityService availabilityService;

    @Transactional
    public AvailabilityResponse createAvailability(UUID storeId, UUID resourceId,
                                                   CreateAvailabilityRequest request) {

        Resource resource = findResourceInStore(storeId, resourceId);

        if (request.startTime().isAfter(request.endTime())
                || request.startTime().equals(request.endTime())) {
            throw new BusinessException(
                    "startTime must be before endTime", HttpStatus.BAD_REQUEST);
        }

        boolean alreadyExists = availabilityRepository
                .findByResourceIdAndDayOfWeekAndActiveTrue(
                        resourceId, request.dayOfWeek())
                .isPresent();

        if (alreadyExists) {
            throw new BusinessException(
                    "Availability already exists for this day", HttpStatus.CONFLICT);
        }

        ResourceAvailability availability = ResourceAvailability.builder()
                .resource(resource)
                .dayOfWeek(request.dayOfWeek())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .active(true)
                .build();

        availabilityRepository.save(availability);
        return toAvailabilityResponse(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> findAvailabilities(UUID storeId, UUID resourceId) {
        findResourceInStore(storeId, resourceId);
        return availabilityRepository.findByResourceIdAndActiveTrue(resourceId)
                .stream()
                .map(this::toAvailabilityResponse)
                .toList();
    }

    @Transactional
    public void deleteAvailability(UUID storeId, UUID resourceId, UUID availabilityId) {
        findResourceInStore(storeId, resourceId);

        ResourceAvailability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() -> new BusinessException(
                        "Availability not found", HttpStatus.NOT_FOUND));

        if (!availability.getResource().getId().equals(resourceId)) {
            throw new BusinessException(
                    "Availability does not belong to this resource",
                    HttpStatus.BAD_REQUEST);
        }

        availability.setActive(false);
        availabilityRepository.save(availability);
    }

    @Transactional
    public AvailabilityExceptionResponse createException(UUID storeId, UUID resourceId,
                                                         CreateAvailabilityExceptionRequest request) {

        Resource resource = findResourceInStore(storeId, resourceId);

        if (request.date().isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "Cannot create exception for past dates", HttpStatus.BAD_REQUEST);
        }

        if (request.available()) {
            if (request.startTime() == null || request.endTime() == null) {
                throw new BusinessException(
                        "startTime and endTime are required when available is true",
                        HttpStatus.BAD_REQUEST);
            }
            if (request.startTime().isAfter(request.endTime())
                    || request.startTime().equals(request.endTime())) {
                throw new BusinessException(
                        "startTime must be before endTime", HttpStatus.BAD_REQUEST);
            }
        }

        exceptionRepository.findByResourceIdAndDate(resourceId, request.date())
                .ifPresent(e -> {
                    throw new BusinessException(
                            "Exception already exists for this date", HttpStatus.CONFLICT);
                });

        ResourceAvailabilityException exception = ResourceAvailabilityException.builder()
                .resource(resource)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .available(request.available())
                .build();

        exceptionRepository.save(exception);
        return toExceptionResponse(exception);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityExceptionResponse> findExceptions(UUID storeId, UUID resourceId) {
        findResourceInStore(storeId, resourceId);
        return exceptionRepository.findByResourceIdOrderByDateAsc(resourceId)
                .stream()
                .map(this::toExceptionResponse)
                .toList();
    }

    @Transactional
    public void deleteException(UUID storeId, UUID resourceId, UUID exceptionId) {
        findResourceInStore(storeId, resourceId);

        ResourceAvailabilityException exception = exceptionRepository
                .findById(exceptionId)
                .orElseThrow(() -> new BusinessException(
                        "Exception not found", HttpStatus.NOT_FOUND));

        if (!exception.getResource().getId().equals(resourceId)) {
            throw new BusinessException(
                    "Exception does not belong to this resource",
                    HttpStatus.BAD_REQUEST);
        }

        exceptionRepository.delete(exception);
    }

    @Transactional(readOnly = true)
    public List<java.time.LocalDateTime> findSlots(UUID storeId, UUID resourceId,
                                                   LocalDate date, int durationMinutes) {
        findResourceInStore(storeId, resourceId);

        Resource resource = resourceRepository.findById(resourceId).get();

        return availabilityService.getSlotsForResource(resource, date, durationMinutes);
    }

    private Resource findResourceInStore(UUID storeId, UUID resourceId) {
        return resourceRepository.findByIdAndStoreId(resourceId, storeId)
                .orElseThrow(() -> new BusinessException(
                        "Resource not found", HttpStatus.NOT_FOUND));
    }

    private AvailabilityResponse toAvailabilityResponse(ResourceAvailability a) {
        return new AvailabilityResponse(
                a.getId(),
                a.getDayOfWeek(),
                a.getStartTime(),
                a.getEndTime(),
                a.isActive()
        );
    }

    private AvailabilityExceptionResponse toExceptionResponse(ResourceAvailabilityException e) {
        return new AvailabilityExceptionResponse(
                e.getId(),
                e.getDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.isAvailable()
        );
    }
}