package com.easyorder.api.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.easyorder.api.backend.config.RedisPubSubConfig;
import com.easyorder.api.backend.dto.RedisNotificationMessage;
import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class RedisPublisherServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisPublisherService redisPublisherService;

    private SseTopicEvent event;

    @BeforeEach
    void setUp() {
        event = new SseTopicEvent(SseTopic.PEDIDOS, 1L);
    }

    @Test
    @DisplayName("Debe serializar el mensaje y publicarlo en el canal Redis de SSE")
    void publish_eventoValido_publicaEnRedis() throws Exception {
        when(objectMapper.writeValueAsString(any(RedisNotificationMessage.class)))
                .thenReturn("{\"tenantId\":1,\"topic\":\"pedidos\"}");

        redisPublisherService.publish(event);

        verify(objectMapper).writeValueAsString(any(RedisNotificationMessage.class));
        verify(redisTemplate).convertAndSend(eq(RedisPubSubConfig.SSE_CHANNEL), eq("{\"tenantId\":1,\"topic\":\"pedidos\"}"));
    }

    @Test
    @DisplayName("Debe capturar JsonProcessingException sin propagar error si falla la serialización")
    void publish_errorSerializacion_capturaExcepcion() throws Exception {
        when(objectMapper.writeValueAsString(any(RedisNotificationMessage.class)))
                .thenThrow(new JsonProcessingException("Error de serialización") {});

        redisPublisherService.publish(event);

        verify(objectMapper).writeValueAsString(any(RedisNotificationMessage.class));
    }
}
