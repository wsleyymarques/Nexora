package com.nexora.repository;

import com.nexora.model.entity.Resource;
import com.nexora.model.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {

    List<Resource> findByStoreIdAndTypeAndActiveTrue(UUID storeId, ResourceType type);

    List<Resource> findByStoreIdAndActiveTrue(UUID storeId);
}