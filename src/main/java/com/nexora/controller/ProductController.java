package com.nexora.controller;

import com.nexora.audit.Auditable;
import com.nexora.dto.request.ProductCreateRequest;
import com.nexora.dto.request.ProductUpdateRequest;
import com.nexora.dto.response.ProductResponse;
import com.nexora.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "PRODUCT_CREATED", entityType = "PRODUCT")
    public ResponseEntity<ProductResponse> create(
            @PathVariable UUID storeId,
            @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(storeId, request));
    }

    @GetMapping
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<List<ProductResponse>> list(
            @PathVariable UUID storeId,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(productService.list(storeId, active));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<ProductResponse> findById(
            @PathVariable UUID storeId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.findById(storeId, productId));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "PRODUCT_UPDATED", entityType = "PRODUCT")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.update(storeId, productId, request));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("@securityUtils.isStoreAdmin(#storeId)")
    @Auditable(action = "PRODUCT_DELETED", entityType = "PRODUCT")
    public ResponseEntity<Void> delete(
            @PathVariable UUID storeId,
            @PathVariable UUID productId) {
        productService.delete(storeId, productId);
        return ResponseEntity.noContent().build();
    }
}
