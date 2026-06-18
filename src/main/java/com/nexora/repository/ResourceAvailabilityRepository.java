package com.nexora.repository;

import com.nexora.model.entity.ResourceAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceAvailabilityRepository extends JpaRepository<ResourceAvailability, UUID> {

    List<ResourceAvailability> findByResourceIdAndActiveTrue(UUID resourceId);

    Optional<ResourceAvailability> findByResourceIdAndDayOfWeekAndActiveTrue(UUID resourceId, DayOfWeek dayOfWeek);
}