package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.easyorder.api.backend.dto.SseTicketResponse;
import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.service.SseNotificationService;
import com.easyorder.api.backend.service.SseTicketService;
import com.easyorder.api.backend.tenant.TenantContext;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/sse", "/api/v1/sse"})
@RequiredArgsConstructor
public class SseController {

  private final SseNotificationService sseNotificationService;
  private final SseTicketService sseTicketService;

  @PostMapping("/ticket")
  public ResponseEntity<SseTicketResponse> generarTicket(Authentication authentication) {
    Long tenantId = TenantContext.get();

    if (tenantId == null) {
      throw new IllegalStateException("No se pudo determinar el tenant para generar el ticket SSE");
    }

    String username = authentication != null ? authentication.getName() : "anonymous";
    var authorities = authentication != null ? authentication.getAuthorities() : List.<GrantedAuthority>of();

    return ResponseEntity.ok(sseTicketService.createTicket(tenantId, username, authorities));
  }

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