package com.nexora.service;

import com.nexora.dto.request.InviteStoreMemberRequest;
import com.nexora.dto.request.UpdateStoreMemberRoleRequest;
import com.nexora.dto.response.StoreMemberResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.StoreMember;
import com.nexora.model.entity.User;
import com.nexora.model.enums.StoreRole;
import com.nexora.repository.StoreMemberRepository;
import com.nexora.repository.StoreRepository;
import com.nexora.repository.UserRepository;
import com.nexora.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreMemberService {

    private final StoreMemberRepository storeMemberRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public StoreMemberResponse invite(UUID storeId, InviteStoreMemberRequest request) {

        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(
                        "Store not found", HttpStatus.NOT_FOUND));

        if (storeMemberRepository.existsByUserIdAndStoreId(request.userId(), storeId)) {
            throw new BusinessException(
                    "User is already a member", HttpStatus.CONFLICT);
        }

        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(
                        "User not found", HttpStatus.NOT_FOUND));

        var member = StoreMember.builder()
                .store(store)
                .user(user)
                .role(request.role())
                .build();

        storeMemberRepository.save(member);
        return toResponse(member);
    }

    public List<StoreMemberResponse> findByStore(UUID storeId) {
        return storeMemberRepository.findByStoreId(storeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StoreMemberResponse updateRole(UUID storeId, UUID memberId,
                                          UpdateStoreMemberRoleRequest request) {

        var member = findMemberInStore(storeId, memberId);

        var currentUser = securityUtils.getCurrentUser();
        if (member.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "Cannot change your own role", HttpStatus.BAD_REQUEST);
        }

        if (member.getRole() == StoreRole.SUPER_ADMIN
                && request.role() != StoreRole.SUPER_ADMIN) {
            validateNotLastAdmin(storeId);
        }

        member.setRole(request.role());
        storeMemberRepository.save(member);
        return toResponse(member);
    }

    @Transactional
    public void remove(UUID storeId, UUID memberId) {

        var member = findMemberInStore(storeId, memberId);

        var currentUser = securityUtils.getCurrentUser();
        if (member.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "Cannot remove yourself", HttpStatus.BAD_REQUEST);
        }

        if (member.getRole() == StoreRole.SUPER_ADMIN) {
            validateNotLastAdmin(storeId);
        }

        storeMemberRepository.delete(member);
    }

    private StoreMember findMemberInStore(UUID storeId, UUID memberId) {
        var member = storeMemberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        "Member not found", HttpStatus.NOT_FOUND));

        if (!member.getStore().getId().equals(storeId)) {
            throw new BusinessException(
                    "Member does not belong to this store", HttpStatus.BAD_REQUEST);
        }

        return member;
    }

    private void validateNotLastAdmin(UUID storeId) {
        long adminCount = storeMemberRepository.findByStoreId(storeId)
                .stream()
                .filter(m -> m.getRole() == StoreRole.SUPER_ADMIN)
                .count();

        if (adminCount <= 1) {
            throw new BusinessException(
                    "Cannot remove the last SUPER_ADMIN", HttpStatus.BAD_REQUEST);
        }
    }

    private StoreMemberResponse toResponse(StoreMember member) {
        User user = member.getUser();
        return new StoreMemberResponse(
                member.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}