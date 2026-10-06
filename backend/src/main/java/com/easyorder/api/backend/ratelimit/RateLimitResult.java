package com.easyorder.api.backend.ratelimit;

public record RateLimitResult(
        boolean allowed,
        long limit,
        long remaining,
        long resetSeconds
) {
}
