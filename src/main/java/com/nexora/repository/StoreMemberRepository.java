package com.nexora.repository;
import com.nexora.model.entity.StoreMember;
import com.nexora.model.enums.StoreRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface StoreMemberRepository extends JpaRepository<StoreMember, UUID> {
    List<StoreMember> findByUserId(UUID userId);
    List<StoreMember> findByStoreId(UUID storeId);
    Optional<StoreMember> findByUserIdAndStoreId(UUID userId, UUID storeId);
    boolean existsByStoreIdAndRole(UUID storeId, StoreRole role);
    boolean existsByUserIdAndStoreId(
            UUID userId,
            UUID storeId
    );

    boolean existsByUserIdAndStoreIdAndRole(
            UUID userId,
            UUID storeId,
            StoreRole role
    );
}
