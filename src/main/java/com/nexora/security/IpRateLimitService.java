package com.nexora.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IpRateLimitService {

    private static final int MAX_REQUESTS = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final Map<String, RequestBucket> buckets =
            new ConcurrentHashMap<>();

    public boolean allowRequest(String ip) {

        RequestBucket bucket =
                buckets.computeIfAbsent(
                        ip,
                        key -> new RequestBucket()
                );

        synchronized (bucket) {

            LocalDateTime now = LocalDateTime.now();

            if (bucket.getExpiresAt().isBefore(now)) {
                bucket.reset(now.plus(WINDOW));
            }

            if (bucket.getCount() >= MAX_REQUESTS) {
                return false;
            }

            bucket.increment();

            return true;
        }
    }
}