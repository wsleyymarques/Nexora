package com.nexora.repository;

import com.nexora.model.entity.RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegisteredClientRepository
        extends JpaRepository<RegisteredClient, UUID> {

    Optional<RegisteredClient> findByClientKeyHash(String hash);

    List<RegisteredClient> findByStoreId(UUID storeId);

    boolean existsByNameAndStoreId(String name, UUID storeId);

    boolean existsByAllowedOriginAndActiveTrue(String allowedOrigin);

}
