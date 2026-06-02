package com.nexora.repository;
import com.nexora.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByStoreId(UUID storeId);
    Optional<Customer> findByStoreIdAndPhone(UUID storeId, String phone);
}
