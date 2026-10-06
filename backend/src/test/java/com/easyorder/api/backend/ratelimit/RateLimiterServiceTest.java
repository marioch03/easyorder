package com.easyorder.api.backend.ratelimit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private RateLimiterService rateLimiterService;

    @Test
    @DisplayName("Debe permitir consumo cuando el contador está por debajo del límite")
    void tryConsume_dentroDelLimite_retornaAllowedTrue() {
        // Simular que el script retorna current = 1, ttl = 59
        when(redisTemplate.execute(any(RedisScript.class), eq(List.of("rl:login:127.0.0.1")), eq("60")))
                .thenReturn(List.of(1L, 59L));

        RateLimitResult result = rateLimiterService.tryConsume("rl:login:127.0.0.1", RateLimitTier.AUTH_LOGIN);

        assertThat(result.allowed()).isTrue();
        assertThat(result.limit()).isEqualTo(5L);
        assertThat(result.remaining()).isEqualTo(4L);
        assertThat(result.resetSeconds()).isEqualTo(59L);
    }

    @Test
    @DisplayName("Debe bloquear consumo cuando el contador excede el límite máximo")
    void tryConsume_excedeLimite_retornaAllowedFalse() {
        // Simular que el script retorna current = 6 (límite es 5), ttl = 45
        when(redisTemplate.execute(any(RedisScript.class), eq(List.of("rl:login:127.0.0.1")), eq("60")))
                .thenReturn(List.of(6L, 45L));

        RateLimitResult result = rateLimiterService.tryConsume("rl:login:127.0.0.1", RateLimitTier.AUTH_LOGIN);

        assertThat(result.allowed()).isFalse();
        assertThat(result.limit()).isEqualTo(5L);
        assertThat(result.remaining()).isEqualTo(0L);
        assertThat(result.resetSeconds()).isEqualTo(45L);
    }

    @Test
    @DisplayName("Debe aplicar fallback fail-open si Redis arroja una excepción")
    void tryConsume_errorEnRedis_retornaFallbackFailOpen() {
        when(redisTemplate.execute(any(RedisScript.class), eq(List.of("rl:public:127.0.0.1")), eq("60")))
                .thenThrow(new RedisSystemException("Conexión perdida con Valkey/Redis", new RuntimeException()));

        RateLimitResult result = rateLimiterService.tryConsume("rl:public:127.0.0.1", RateLimitTier.PUBLIC_DISCOVERY);

        assertThat(result.allowed()).isTrue();
        assertThat(result.limit()).isEqualTo(30L);
        assertThat(result.remaining()).isEqualTo(30L);
        assertThat(result.resetSeconds()).isEqualTo(60L);
    }
}
