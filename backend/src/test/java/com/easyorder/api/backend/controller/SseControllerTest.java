package com.easyorder.api.backend.controller;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.easyorder.api.backend.dto.SseTicketResponse;
import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.service.SseNotificationService;
import com.easyorder.api.backend.service.SseSecurityService;
import com.easyorder.api.backend.service.SseTicketService;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class SseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SseNotificationService sseNotificationService;

    @Mock
    private SseTicketService sseTicketService;

    @Mock
    private SseSecurityService sseSecurityService;

    @InjectMocks
    private SseController sseController;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);

        mockMvc = MockMvcBuilders.standaloneSetup(sseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("GET /api/v1/sse/stream/{topic} debe suscribir al cliente y retornar HTTP 200 con MediaType TEXT_EVENT_STREAM")
    void stream_topicValido_retorna200() throws Exception {
        SseEmitter emitter = new SseEmitter();
        when(sseNotificationService.suscribir(1L, "pedidos")).thenReturn(emitter);

        mockMvc.perform(get("/sse/stream/pedidos")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());

        verify(sseNotificationService).suscribir(1L, "pedidos");
        verify(sseSecurityService).validarAccesoTopic(eq(SseTopic.PEDIDOS), any());
    }

    @Test
    @DisplayName("GET /sse/stream/{topic} con topic inválido debe retornar HTTP 400 Bad Request")
    void stream_topicInvalido_retorna400() throws Exception {
        mockMvc.perform(get("/sse/stream/topic_inexistente")
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic SSE no válido: topic_inexistente"));
    }

    @Test
    @DisplayName("GET /sse/stream/{topic} con rol no autorizado debe retornar HTTP 403 Forbidden")
    void stream_rolNoAutorizado_retorna403() throws Exception {
        doThrow(new AccessDeniedException("No dispone de los permisos necesarios para suscribirse al canal SSE 'kds'"))
                .when(sseSecurityService).validarAccesoTopic(eq(SseTopic.KDS), any());

        mockMvc.perform(get("/sse/stream/kds")
                        .accept(MediaType.ALL))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("No dispone de los permisos necesarios para suscribirse al canal SSE 'kds'"));
    }

    @Test
    @DisplayName("POST /sse/ticket debe generar un ticket temporal efímero con HTTP 200")
    void generarTicket_retorna200ConTicket() throws Exception {
        when(sseTicketService.createTicket(eq(1L), eq("camarero1"), any()))
                .thenReturn(new SseTicketResponse("test-uuid-ticket-123", 30L));

        var auth = new UsernamePasswordAuthenticationToken(
                "camarero1", null, List.of(new SimpleGrantedAuthority("PERSONAL")));

        mockMvc.perform(post("/sse/ticket")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticket").value("test-uuid-ticket-123"))
                .andExpect(jsonPath("$.expiresInSeconds").value(30));
    }
}
