package com.easyorder.api.backend.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.SseTicketData;
import com.easyorder.api.backend.dto.SseTicketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseTicketService {

    public static final String TICKET_KEY_PREFIX = "sse:ticket:";
    public static final long TICKET_EXPIRATION_SECONDS = 30L;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public SseTicketResponse createTicket(Long tenantId, String username, Collection<? extends GrantedAuthority> authorities) {
        String ticket = UUID.randomUUID().toString();
        List<String> roles = authorities != null
                ? authorities.stream().map(GrantedAuthority::getAuthority).toList()
                : List.of();

        SseTicketData data = new SseTicketData(tenantId, username, roles);

        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(
                    TICKET_KEY_PREFIX + ticket,
                    json,
                    Duration.ofSeconds(TICKET_EXPIRATION_SECONDS));
            log.debug("Ticket SSE efímero generado para user={}, tenant={}", username, tenantId);
            return new SseTicketResponse(ticket, TICKET_EXPIRATION_SECONDS);
        } catch (JsonProcessingException e) {
            log.error("Error serializando datos para ticket SSE", e);
            throw new IllegalStateException("Error al generar ticket SSE", e);
        }
    }

    public Optional<SseTicketData> redeemTicket(String ticket) {
        if (ticket == null || ticket.isBlank()) {
            return Optional.empty();
        }

        String key = TICKET_KEY_PREFIX + ticket.trim();
        String json = redisTemplate.opsForValue().getAndDelete(key);

        if (json == null || json.isBlank()) {
            log.debug("Ticket SSE no encontrado o ya consumido: {}", ticket);
            return Optional.empty();
        }

        try {
            SseTicketData data = objectMapper.readValue(json, SseTicketData.class);
            log.debug("Ticket SSE canjeado con éxito para user={}, tenant={}", data.username(), data.tenantId());
            return Optional.of(data);
        } catch (JsonProcessingException e) {
            log.error("Error deserializando datos del ticket SSE: {}", ticket, e);
            return Optional.empty();
        }
    }
}
