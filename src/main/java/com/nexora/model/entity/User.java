package com.nexora.model.entity;

import com.nexora.model.enums.UserOrigin;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(length = 150)
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Customer> customerProfiles = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserOrigin origin = UserOrigin.DIRECT;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoreMember> storeMembers = new ArrayList<>();
}
