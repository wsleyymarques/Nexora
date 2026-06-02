package com.nexora.repository;

import com.nexora.model.entity.Product;
import com.nexora.model.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStoreId(UUID storeId);
    List<Product> findByStoreIdAndActive(UUID storeId, boolean active);
    List<Product> findByStoreIdAndType(UUID storeId, ProductType type);
    Optional<Product> findByIdAndStoreId(UUID id, UUID storeId);
}
