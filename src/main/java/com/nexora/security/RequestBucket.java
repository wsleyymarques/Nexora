package com.nexora.security;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestBucket {

    private int count = 0;

    private LocalDateTime expiresAt =
            LocalDateTime.now().plusMinutes(1);

    public void increment() {
        count++;
    }

    public void reset(LocalDateTime expiresAt) {
        this.count = 0;
        this.expiresAt = expiresAt;
    }
}