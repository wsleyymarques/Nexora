package com.nexora.repository;

import com.nexora.model.entity.ResourceAvailabilityException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceAvailabilityExceptionRepository extends JpaRepository<ResourceAvailabilityException, UUID> {
    List<ResourceAvailabilityException> findByResourceIdOrderByDateAsc(UUID resourceId);
    Optional<ResourceAvailabilityException> findByResourceIdAndDate(UUID resourceId, LocalDate date);
}