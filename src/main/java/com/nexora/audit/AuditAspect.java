package com.nexora.audit;

import com.nexora.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                resolveEntityId(joinPoint, result)
        );
    }

    private UUID resolveEntityId(JoinPoint joinPoint, Object result) {
        Object payload = unwrapResponseEntity(result);

        UUID entityId = extractUuid(payload);
        if (entityId != null) {
            return entityId;
        }

        entityId = extractNestedUuid(payload, "user");
        if (entityId != null) {
            return entityId;
        }

        return extractUuidArgument(joinPoint.getArgs());
    }

    private Object unwrapResponseEntity(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getBody();
        }

        return result;
    }

    private UUID extractNestedUuid(Object payload, String accessor) {
        Object nested = invokeAccessor(payload, accessor);
        if (nested == null) {
            return null;
        }

        return extractUuid(nested);
    }

    private UUID extractUuidArgument(Object[] args) {
        List<Object> reversedArgs = Arrays.asList(args);

        for (int i = reversedArgs.size() - 1; i >= 0; i--) {
            Object arg = reversedArgs.get(i);
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }

        return null;
    }

    private UUID extractUuid(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof UUID uuid) {
            return uuid;
        }

        Object direct = invokeAccessor(value, "id");
        if (direct instanceof UUID uuid) {
            return uuid;
        }

        direct = invokeAccessor(value, "getId");
        if (direct instanceof UUID uuid) {
            return uuid;
        }

        return null;
    }

    private Object invokeAccessor(Object target, String accessor) {
        if (target == null) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(accessor);
            return method.invoke(target);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
