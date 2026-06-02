package com.nexora.repository;
import com.nexora.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByCustomerId(UUID customerId);
    @Query("SELECT a FROM Appointment a WHERE a.orderItem.order.store.id = :storeId AND a.scheduledAt BETWEEN :start AND :end")
    List<Appointment> findByStoreIdAndPeriod(UUID storeId, LocalDateTime start, LocalDateTime end);
}
