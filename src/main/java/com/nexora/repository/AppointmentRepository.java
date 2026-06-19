package com.nexora.repository;

import com.nexora.model.entity.Appointment;
import com.nexora.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.resource.id = :resourceId
        AND a.status NOT IN ('CANCELLED')
        AND a.scheduledAt < :end
        AND FUNCTION('TIMESTAMPADD', MINUTE, a.durationMinutes, a.scheduledAt) > :start
    """)
    List<Appointment> findConflicting(
            @Param("resourceId") UUID resourceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Appointment> findByCustomerIdOrderByScheduledAtDesc(UUID customerId);

    List<Appointment> findByResourceIdAndStatusOrderByScheduledAtAsc(UUID resourceId, AppointmentStatus status);
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.orderItem.order.store.id = :storeId
    """)
    List<Appointment> findByStoreId(@Param("storeId") UUID storeId);

    List<Appointment> findByCustomerUserIdOrderByScheduledAtDesc(UUID userId);
}