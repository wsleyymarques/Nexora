package com.nexora.controller;

import com.nexora.audit.Auditable;
import com.nexora.dto.request.InviteStoreMemberRequest;
import com.nexora.dto.request.UpdateStoreMemberRoleRequest;
import com.nexora.dto.response.StoreMemberResponse;
import com.nexora.service.StoreMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/members")
@RequiredArgsConstructor
public class StoreMemberController {

    private final StoreMemberService storeMemberService;

    @PostMapping
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "MEMBER_INVITED", entityType = "STORE_MEMBER")
    public ResponseEntity<StoreMemberResponse> invite(
            @PathVariable UUID storeId,
            @Valid @RequestBody InviteStoreMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeMemberService.invite(storeId, request));
    }

    @GetMapping
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<List<StoreMemberResponse>> findByStore(
            @PathVariable UUID storeId) {
        return ResponseEntity.ok(storeMemberService.findByStore(storeId));
    }

    @PatchMapping("/{memberId}/role")
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "MEMBER_ROLE_UPDATED", entityType = "STORE_MEMBER")
    public ResponseEntity<StoreMemberResponse> updateRole(
            @PathVariable UUID storeId,
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateStoreMemberRoleRequest request) {
        return ResponseEntity.ok(
                storeMemberService.updateRole(storeId, memberId, request));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "MEMBER_REMOVED", entityType = "STORE_MEMBER")
    public ResponseEntity<Void> remove(
            @PathVariable UUID storeId,
            @PathVariable UUID memberId) {
        storeMemberService.remove(storeId, memberId);
        return ResponseEntity.noContent().build();
    }
}