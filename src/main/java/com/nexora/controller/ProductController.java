package com.nexora.controller;

import com.nexora.dto.request.CreateProductRequest;
import com.nexora.dto.request.UpdateProductRequest;
import com.nexora.dto.response.ProductResponse;
import com.nexora.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @PathVariable UUID storeId,
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(storeId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(
            @PathVariable UUID storeId,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(productService.list(storeId, active));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findById(
            @PathVariable UUID storeId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.findById(storeId, productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(storeId, productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID storeId,
            @PathVariable UUID productId) {
        productService.delete(storeId, productId);
        return ResponseEntity.noContent().build();
    }
}
