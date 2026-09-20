package com.easyorder.api.backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.service.SseNotificationService;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class SseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SseNotificationService sseNotificationService;

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
    @DisplayName("GET /sse/stream/{topic} debe suscribir al cliente y retornar HTTP 200 con MediaType TEXT_EVENT_STREAM")
    void stream_topicValido_retorna200() throws Exception {
        SseEmitter emitter = new SseEmitter();
        when(sseNotificationService.suscribir(1L, "pedidos")).thenReturn(emitter);

        mockMvc.perform(get("/sse/stream/pedidos")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());

        verify(sseNotificationService).suscribir(1L, "pedidos");
    }

    @Test
    @DisplayName("GET /sse/stream/{topic} con topic inválido debe retornar HTTP 400 Bad Request")
    void stream_topicInvalido_retorna400() throws Exception {
        mockMvc.perform(get("/sse/stream/topic_inexistente")
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic SSE no válido: topic_inexistente"));
    }
}
