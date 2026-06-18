package com.nexora.controller;

import com.nexora.audit.Auditable;
import com.nexora.dto.request.CreateOrderRequest;
import com.nexora.dto.request.UpdateOrderStatusRequest;
import com.nexora.dto.response.OrderResponse;
import com.nexora.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("@securityUtils.canCreateOrder(#request.storeId(), #request.customerId())")
    @Auditable(action = "ORDER_CREATED", entityType = "ORDER")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("@securityUtils.canAccessOrder(#orderId)")
    public ResponseEntity<OrderResponse> findById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.findById(orderId));
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("@securityUtils.isStoreMember(#storeId)")
    public ResponseEntity<List<OrderResponse>> findByStore(@PathVariable UUID storeId) {
        return ResponseEntity.ok(orderService.findByStore(storeId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("@securityUtils.canAccessCustomerOrders(#customerId)")
    public ResponseEntity<List<OrderResponse>> findByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(orderService.findByCustomer(customerId));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("@securityUtils.canManageOrder(#orderId)")
    @Auditable(action = "ORDER_STATUS_CHANGED", entityType = "ORDER")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request));
    }
}
