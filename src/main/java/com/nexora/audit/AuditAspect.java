package com.nexora.audit;

import com.nexora.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(
            value = "@annotation(auditable)",
            returning = "result"
    )
    public void audit(
            JoinPoint joinPoint,
            Auditable auditable,
            Object result
    ) {

        auditService.log(
                auditable.action(),
                auditable.entityType(),
                null
        );
    }
}