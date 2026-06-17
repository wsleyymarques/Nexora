package com.nexora.service;

import com.nexora.dto.request.StoreCreateRequest;
import com.nexora.dto.response.StoreResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Store;
import com.nexora.model.entity.StoreMember;
import com.nexora.model.enums.StoreRole;
import com.nexora.repository.StoreRepository;
import com.nexora.repository.StoreMemberRepository;
import com.nexora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMemberRepository storeMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoreResponse create(StoreCreateRequest request) {
        if (storeRepository.existsBySlug(request.slug())) {
            throw new BusinessException("Slug already taken", HttpStatus.CONFLICT);
        }

        var store = Store.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .build();

        storeRepository.save(store);

        // First member is SUPER_ADMIN
        var currentUser = currentUser();
        var member = StoreMember.builder()
                .user(currentUser)
                .store(store)
                .role(StoreRole.SUPER_ADMIN)
                .build();

        storeMemberRepository.save(member);

        return toResponse(store);
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> myStores() {
        var user = currentUser();
        return storeMemberRepository.findByUserId(user.getId()).stream()
                .map(m -> toResponse(m.getStore()))
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreResponse findById(UUID id) {
        return storeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));
    }

    private com.nexora.model.entity.User currentUser() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(store.getId(), store.getName(), store.getSlug(),
                store.getDescription(), store.isActive());
    }
}
