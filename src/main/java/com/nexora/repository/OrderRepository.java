package com.nexora.repository;
import com.nexora.model.entity.Order;
import com.nexora.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStoreId(UUID storeId);
    List<Order> findByCustomerId(UUID customerId);
    List<Order> findByStoreIdAndStatus(UUID storeId, OrderStatus status);
}
