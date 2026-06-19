package com.nexora.controller;

import com.nexora.exception.BusinessException;
import com.nexora.model.entity.RegisteredClient;
import com.nexora.model.entity.Store;
import com.nexora.repository.RegisteredClientRepository;
import com.nexora.repository.StoreRepository;
import com.nexora.service.RegisteredClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/registered-clients")
@RequiredArgsConstructor
public class RegisteredClientController {

    private final RegisteredClientService registeredClientService;
    private final RegisteredClientRepository registeredClientRepository;
    private final StoreRepository storeRepository;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterClientRequest request) {

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new BusinessException(
                        "Store não encontrada",
                        HttpStatus.NOT_FOUND
                ));

        String rawKey = registeredClientService.generateClientKey();
        String hash = registeredClientService.hashKey(rawKey);

        RegisteredClient client = RegisteredClient.builder()
                .store(store)
                .name(request.name())
                .clientKeyHash(hash)
                .allowedOrigin(request.allowedOrigin())
                .allowedIp(request.allowedIp())
                .active(true)
                .build();

        registeredClientRepository.save(client);

        return ResponseEntity.ok(new RegisterClientResponse(client.getId(), rawKey));
    }

    public record RegisterClientRequest(
            UUID storeId,
            String name,
            String allowedOrigin,
            String allowedIp
    ) {}

    public record RegisterClientResponse(
            UUID id,
            String clientKey
    ) {}
}