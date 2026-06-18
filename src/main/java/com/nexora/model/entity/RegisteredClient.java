package com.nexora.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registered_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredClient extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String clientKey;

    @Column(length = 255)
    private String allowedOrigin;

    @Column(length = 100)
    private String allowedIp;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}