package com.nexora.controller;
import com.nexora.dto.request.StoreCreateRequest;
import com.nexora.dto.response.StoreResponse;
import com.nexora.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody StoreCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.create(request));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<StoreResponse>> myStores() {
        return ResponseEntity.ok(storeService.myStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(storeService.findById(id));
    }
}
