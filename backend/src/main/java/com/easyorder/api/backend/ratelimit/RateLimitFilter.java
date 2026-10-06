package com.easyorder.api.backend.ratelimit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Permitir solicitudes preflight CORS sin restricción de tasa
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // Omitir endpoints no relacionados con la API de negocio (documentación, salud)
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitTier tier = resolveTier(request.getMethod(), path);
        String clientIp = extractClientIp(request);
        String sessionCode = request.getHeader("X-Session-Code");

        String rateLimitKey = resolveKey(tier, clientIp, sessionCode);

        RateLimitResult result = rateLimiterService.tryConsume(rateLimitKey, tier);

        // Añadir cabeceras informativas de Rate Limit según el estándar
        response.setHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(result.resetSeconds()));

        if (!result.allowed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(result.resetSeconds()));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            Map<String, Object> errorBody = Map.of(
                    "timestamp", Instant.now().toString(),
                    "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                    "error", "Too Many Requests",
                    "message", "Demasiadas peticiones. Por favor, espere " + result.resetSeconds() + " segundos antes de reintentar.",
                    "path", path
            );

            objectMapper.writeValue(response.getWriter(), errorBody);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitTier resolveTier(String method, String path) {
        if ("/api/v1/auth/login".equals(path) && HttpMethod.POST.matches(method)) {
            return RateLimitTier.AUTH_LOGIN;
        }
        if ("/api/v1/cliente/pedidos".equals(path) && HttpMethod.POST.matches(method)) {
            return RateLimitTier.CLIENTE_PEDIDO;
        }
        if (path.startsWith("/api/v1/public/")) {
            return RateLimitTier.PUBLIC_DISCOVERY;
        }
        if (path.startsWith("/api/v1/cliente/")) {
            return RateLimitTier.CLIENTE_GENERAL;
        }
        return RateLimitTier.API_GLOBAL;
    }

    private String resolveKey(RateLimitTier tier, String clientIp, String sessionCode) {
        if ((tier == RateLimitTier.CLIENTE_PEDIDO || tier == RateLimitTier.CLIENTE_GENERAL)
                && sessionCode != null && !sessionCode.isBlank()) {
            return tier.getKeyPrefix() + sessionCode.trim();
        }
        return tier.getKeyPrefix() + clientIp;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
