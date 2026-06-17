package com.nexora.repository;

import com.nexora.model.entity.OtpCode;
import com.nexora.model.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {

    Optional<OtpCode> findTopByTargetAndTypeAndUsedFalseOrderByCreatedAtDesc(
            String target,
            OtpType type
    );

}