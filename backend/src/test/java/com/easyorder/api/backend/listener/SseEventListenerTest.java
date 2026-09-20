package com.easyorder.api.backend.listener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.service.RedisPublisherService;

@ExtendWith(MockitoExtension.class)
class SseEventListenerTest {

    @Mock
    private RedisPublisherService redisPublisherService;

    @InjectMocks
    private SseEventListener sseEventListener;

    @Test
    @DisplayName("onSseTopicEvent debe delegar la publicación del evento a RedisPublisherService")
    void onSseTopicEvent_delegaEnRedisPublisher() {
        SseTopicEvent event = new SseTopicEvent(SseTopic.PEDIDOS, 1L);

        sseEventListener.onSseTopicEvent(event);

        verify(redisPublisherService).publish(event);
    }
}
