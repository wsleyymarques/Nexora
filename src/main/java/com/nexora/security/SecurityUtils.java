package com.nexora.security;

import com.nexora.exception.BusinessException;
import com.nexora.model.entity.User;
import com.nexora.model.enums.StoreRole;
import com.nexora.repository.StoreMemberRepository;
import com.nexora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final StoreMemberRepository storeMemberRepository;
    private final CurrentRequest currentRequest;

    public User getCurrentUser() {

        User user = currentRequest.getUser();

        if (user == null) {
            throw new BusinessException(
                    "User not authenticated",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return user;
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public boolean isStoreMember(UUID storeId) {

        User user = getCurrentUser();

        return storeMemberRepository.existsByUserIdAndStoreId(
                user.getId(),
                storeId
        );
    }

    public boolean isStoreAdmin(UUID storeId) {

        User user = getCurrentUser();

        return storeMemberRepository.existsByUserIdAndStoreIdAndRole(
                user.getId(),
                storeId,
                StoreRole.SUPER_ADMIN
        );
    }
}