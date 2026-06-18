package com.nexora.service;

import com.nexora.model.entity.AuditLog;
import com.nexora.repository.AuditLogRepository;
import com.nexora.security.CurrentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentRequest currentRequest;

    public void log(
            String action,
            String entityType,
            UUID entityId
    ) {

        AuditLog log = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .user(currentRequest.getUser())
                .ip(currentRequest.getIp())
                .userAgent(currentRequest.getUserAgent())
                .origin(currentRequest.getOrigin())
                .path(currentRequest.getPath())
                .method(currentRequest.getMethod())
                .build();

        auditLogRepository.save(log);
    }
}