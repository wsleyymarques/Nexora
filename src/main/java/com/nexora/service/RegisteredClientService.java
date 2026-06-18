package com.nexora.service;


import com.nexora.exception.BusinessException;
import com.nexora.model.entity.RegisteredClient;
import com.nexora.repository.RegisteredClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RegisteredClientService {

    private final RegisteredClientRepository registeredClientRepository;

    public String generateClientKey() {

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        return "nk_live_" + HexFormat.of().formatHex(bytes);
    }

    public String hashKey(String key) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    key.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Erro ao gerar hash da client key",
                    e
            );
        }
    }

    public RegisteredClient validate(String key) {

        String hash = hashKey(key);

        RegisteredClient client = registeredClientRepository
                .findByClientKeyHash(hash)
                .orElseThrow(() ->
                        new BusinessException(
                                "Client não registrado",
                                HttpStatus.UNAUTHORIZED
                        )
                );

        if (!client.isActive()) {
            throw new BusinessException(
                    "Client desativado",
                    HttpStatus.FORBIDDEN
            );
        }

        client.setLastUsedAt(
                LocalDateTime.now()
        );

        registeredClientRepository.save(client);

        return client;
    }
}