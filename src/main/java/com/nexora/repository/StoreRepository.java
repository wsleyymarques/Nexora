package com.nexora.repository;
import com.nexora.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
