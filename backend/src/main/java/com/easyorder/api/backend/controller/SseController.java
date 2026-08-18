package com.easyorder.api.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.service.SseNotificationService;
import com.easyorder.api.backend.tenant.TenantContext;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

  private final SseNotificationService sseNotificationService;

  @GetMapping(value = "/stream/{topic}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter stream(@PathVariable String topic) {
    SseTopic sseTopic = SseTopic.fromValue(topic);

    Long tenantId = TenantContext.get();

    if (tenantId == null) {
      throw new IllegalStateException("No se pudo determinar el tenant para la conexión SSE");
    }

    return sseNotificationService.suscribir(tenantId, sseTopic.getValue());
  }
}