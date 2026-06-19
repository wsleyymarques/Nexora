package com.nexora.controller;

import com.nexora.audit.Auditable;
import com.nexora.dto.request.CreateResourceRequest;
import com.nexora.dto.request.UpdateResourceRequest;
import com.nexora.dto.response.ResourceResponse;
import com.nexora.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "RESOURCE_CREATED", entityType = "RESOURCE")
    public ResponseEntity<ResourceResponse> create(
            @PathVariable UUID storeId,
            @Valid @RequestBody CreateResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.create(storeId, request));
    }

    @GetMapping
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<List<ResourceResponse>> findByStore(
            @PathVariable UUID storeId) {
        return ResponseEntity.ok(resourceService.findByStore(storeId));
    }

    @GetMapping("/{resourceId}")
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<ResourceResponse> findById(
            @PathVariable UUID storeId,
            @PathVariable UUID resourceId) {
        return ResponseEntity.ok(resourceService.findById(storeId, resourceId));
    }

    @PutMapping("/{resourceId}")
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "RESOURCE_UPDATED", entityType = "RESOURCE")
    public ResponseEntity<ResourceResponse> update(
            @PathVariable UUID storeId,
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateResourceRequest request) {
        return ResponseEntity.ok(resourceService.update(storeId, resourceId, request));
    }

    @DeleteMapping("/{resourceId}")
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "RESOURCE_DEACTIVATED", entityType = "RESOURCE")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID storeId,
            @PathVariable UUID resourceId) {
        resourceService.deactivate(storeId, resourceId);
        return ResponseEntity.noContent().build();
    }
}