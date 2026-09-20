package com.easyorder.api.backend.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.service.RedisPublisherService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseEventListener {

    private final RedisPublisherService redisPublisherService;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onSseTopicEvent(SseTopicEvent event) {
        redisPublisherService.publish(event);
    }
}