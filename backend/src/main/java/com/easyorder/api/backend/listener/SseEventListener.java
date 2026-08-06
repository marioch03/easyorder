package com.easyorder.api.backend.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.service.SseNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseEventListener {

    private final SseNotificationService sseNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSseTopicEvent(SseTopicEvent event) {
        String canal = SseTopic.canal(event.topic(), event.tenantId());
        sseNotificationService.notificar(canal);
    }
}