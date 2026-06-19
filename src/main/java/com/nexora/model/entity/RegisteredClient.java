package com.nexora.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registered_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredClient extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "client_key_hash", nullable = false, unique = true, length = 255)
    private String clientKeyHash;

    @Column(name = "allowed_origin", length = 255)
    private String allowedOrigin;

    @Column(name = "allowed_ip", length = 100)
    private String allowedIp;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
}
