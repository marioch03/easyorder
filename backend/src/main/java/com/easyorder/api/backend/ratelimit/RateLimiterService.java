package com.easyorder.api.backend.ratelimit;

import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final String RATE_LIMIT_LUA = """
            local current = redis.call('INCR', KEYS[1])
            if current == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            local ttl = redis.call('TTL', KEYS[1])
            return {current, ttl}
            """;

    @SuppressWarnings("rawtypes")
    private final RedisScript<List> rateLimitScript = new DefaultRedisScript<>(RATE_LIMIT_LUA, List.class);

    /**
     * Evalúa y descuenta atómicamente un token para la clave y nivel especificados.
     * En caso de indisponibilidad de Redis, aplica una degradación elegante (fail-open)
     * para no interrumpir el servicio.
     */
    public RateLimitResult tryConsume(String key, RateLimitTier tier) {
        long max = tier.getMaxRequests();
        long window = tier.getWindowSeconds();

        try {
            @SuppressWarnings("unchecked")
            List<Long> result = redisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(key),
                    String.valueOf(window));

            if (result != null && result.size() >= 2) {
                long current = ((Number) result.get(0)).longValue();
                long ttl = ((Number) result.get(1)).longValue();
                long resetSeconds = ttl > 0 ? ttl : window;

                if (current <= max) {
                    long remaining = max - current;
                    return new RateLimitResult(true, max, remaining, resetSeconds);
                } else {
                    return new RateLimitResult(false, max, 0, resetSeconds);
                }
            }
        } catch (Exception e) {
            log.warn("Redis no disponible para Rate Limiting en clave {}: {}. Aplicando fallback fail-open.", key, e.getMessage());
        }

        // Fallback fail-open ante fallo de caché
        return new RateLimitResult(true, max, max, window);
    }
}
