package com.nexora.repository;

import com.nexora.model.entity.Resource;
import com.nexora.model.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {

    List<Resource> findByStoreIdAndTypeAndActiveTrue(UUID storeId, ResourceType type);
    Optional<Resource> findByIdAndStoreId(UUID id, UUID storeId);
    List<Resource> findByStoreIdAndActiveTrue(UUID storeId);

}