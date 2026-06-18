package com.nexora.repository;

import com.nexora.model.entity.Order;
import com.nexora.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByStoreIdOrderByCreatedAtDesc(UUID storeId);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<Order> findByStoreIdAndStatusOrderByCreatedAtDesc(UUID storeId, OrderStatus status);
}