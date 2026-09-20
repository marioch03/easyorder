package com.easyorder.api.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.easyorder.api.backend.dto.RedisNotificationMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class RedisSubscriberServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SseNotificationService sseNotificationService;

    @InjectMocks
    private RedisSubscriberService redisSubscriberService;

    @Test
    @DisplayName("Debe deserializar el mensaje y notificar a los clientes locales del tenant y topic")
    void handleMessage_mensajeValido_notificaClientesLocales() throws Exception {
        String jsonMessage = "{\"tenantId\":1,\"topic\":\"pedidos\"}";
        RedisNotificationMessage notification = new RedisNotificationMessage(1L, "pedidos");

        when(objectMapper.readValue(eq(jsonMessage), eq(RedisNotificationMessage.class)))
                .thenReturn(notification);

        redisSubscriberService.handleMessage(jsonMessage);

        verify(sseNotificationService).notificarLocales(1L, "pedidos");
    }

    @Test
    @DisplayName("Debe capturar error sin propagar excepción si el JSON es inválido")
    void handleMessage_jsonInvalido_capturaExcepcion() throws Exception {
        String jsonInvalido = "{json-corrupto}";

        when(objectMapper.readValue(eq(jsonInvalido), eq(RedisNotificationMessage.class)))
                .thenThrow(new JsonParseException(null, "JSON inválido"));

        redisSubscriberService.handleMessage(jsonInvalido);

        verifyNoInteractions(sseNotificationService);
    }
}
