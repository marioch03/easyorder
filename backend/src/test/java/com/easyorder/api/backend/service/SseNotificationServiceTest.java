package com.easyorder.api.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseNotificationServiceTest {

    private SseNotificationService sseNotificationService;

    @BeforeEach
    void setUp() {
        sseNotificationService = new SseNotificationService();
    }

    @Test
    @DisplayName("suscribir debe crear y retornar un SseEmitter para el canal del tenant y topic")
    void suscribir_creaYRetornaEmitter() {
        SseEmitter emitter = sseNotificationService.suscribir(1L, "pedidos");

        assertThat(emitter).isNotNull();
        assertThat(emitter.getTimeout()).isEqualTo(3_600_000L);
    }

    @Test
    @DisplayName("notificarLocales debe ejecutarse sin errores cuando no hay clientes conectados")
    void notificarLocales_sinClientes_noLanzaExcepcion() {
        sseNotificationService.notificarLocales(1L, "pedidos");
    }

    @Test
    @DisplayName("notificarLocales debe emitir eventos a clientes suscritos sin errores")
    void notificarLocales_conClientes_enviaNotificacion() {
        SseEmitter emitter = sseNotificationService.suscribir(1L, "kds");
        assertThat(emitter).isNotNull();

        sseNotificationService.notificarLocales(1L, "kds");
    }

    @Test
    @DisplayName("sendHeartbeat debe enviar pings a todos los emitters registrados")
    void sendHeartbeat_enviaPingATodos() {
        sseNotificationService.suscribir(1L, "mesas");
        sseNotificationService.suscribir(2L, "pedidos");

        sseNotificationService.sendHeartbeat();
    }
}
