package com.easyorder.api.backend.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.easyorder.api.backend.dto.SseTicketData;
import com.easyorder.api.backend.dto.SseTicketResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class SseTicketServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private ObjectMapper objectMapper;
    private SseTicketService sseTicketService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        sseTicketService = new SseTicketService(redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("createTicket debe almacenar ticket con TTL de 30s en Redis y retornar respuesta válida")
    void createTicket_almacenaEnRedisConTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        SseTicketResponse response = sseTicketService.createTicket(
                1L, "admin", List.of(new SimpleGrantedAuthority("ADMIN")));

        assertThat(response.ticket()).isNotBlank();
        assertThat(response.expiresInSeconds()).isEqualTo(30L);

        String expectedKey = SseTicketService.TICKET_KEY_PREFIX + response.ticket();
        verify(valueOperations).set(eq(expectedKey), anyString(), eq(Duration.ofSeconds(30)));
    }

    @Test
    @DisplayName("redeemTicket debe recuperar y eliminar atómicamente el ticket de Redis")
    void redeemTicket_exitoso() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        SseTicketData storedData = new SseTicketData(1L, "camarero1", List.of("PERSONAL"));
        String json = objectMapper.writeValueAsString(storedData);

        String ticket = "test-uuid-456";
        String key = SseTicketService.TICKET_KEY_PREFIX + ticket;
        when(valueOperations.getAndDelete(key)).thenReturn(json);

        Optional<SseTicketData> result = sseTicketService.redeemTicket(ticket);

        assertThat(result).isPresent();
        assertThat(result.get().tenantId()).isEqualTo(1L);
        assertThat(result.get().username()).isEqualTo("camarero1");
        assertThat(result.get().roles()).containsExactly("PERSONAL");

        verify(valueOperations).getAndDelete(key);
    }

    @Test
    @DisplayName("redeemTicket con ticket inexistente o ya canjeado debe retornar Optional.empty()")
    void redeemTicket_inexistente_retornaEmpty() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String ticket = "ticket-expirado";
        String key = SseTicketService.TICKET_KEY_PREFIX + ticket;
        when(valueOperations.getAndDelete(key)).thenReturn(null);

        Optional<SseTicketData> result = sseTicketService.redeemTicket(ticket);

        assertThat(result).isEmpty();
        verify(valueOperations).getAndDelete(key);
    }

    @Test
    @DisplayName("redeemTicket con ticket nulo o vacío debe retornar Optional.empty() sin consultar Redis")
    void redeemTicket_nuloOVacio_retornaEmpty() {
        assertThat(sseTicketService.redeemTicket(null)).isEmpty();
        assertThat(sseTicketService.redeemTicket("   ")).isEmpty();

        verify(redisTemplate, never()).opsForValue();
    }
}
