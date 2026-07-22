package com.easyorder.api.backend.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseNotificationService {

  private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

  public SseEmitter suscribir(String topic) {
    SseEmitter emitter = new SseEmitter(0L);

    emitters.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(emitter);

    emitter.onCompletion(() -> removeEmitter(topic, emitter));
    emitter.onTimeout(() -> removeEmitter(topic, emitter));
    emitter.onError((e) -> removeEmitter(topic, emitter));

    try {
      emitter.send(SseEmitter.event().data("connected"));
    } catch (Exception e) {
      log.error("💥 ERROR al enviar refresh al KDS. Cortando conexión...", e); // 👈 Añade esto
      removeEmitter(topic, emitter);
    }

    return emitter;
  }

  public void notificar(String topic) {
    List<SseEmitter> canalEmitters = emitters.get(topic);
    log.info("notificar('{}') -> {} suscriptores", topic, canalEmitters == null ? 0 : canalEmitters.size());

    if (canalEmitters != null) {
      for (SseEmitter emitter : canalEmitters) {
        synchronized (emitter) {
          try {
            emitter.send(SseEmitter.event().data("refresh"));
          } catch (Exception e) {
            log.error("💥 ERROR al enviar refresh al KDS. Cortando conexión...", e); // 👈 Añade esto
            removeEmitter(topic, emitter);
          }
        }
      }
    }
  }

  @Scheduled(fixedRate = 15000)
  public void sendHeartbeat() {
    for (Map.Entry<String, List<SseEmitter>> entry : emitters.entrySet()) {
      List<SseEmitter> canalEmitters = entry.getValue();
      if (canalEmitters != null && !canalEmitters.isEmpty()) {
        for (SseEmitter emitter : canalEmitters) {
          synchronized (emitter) {
            try {
              emitter.send(SseEmitter.event().comment("ping"));
            } catch (Exception e) {
              removeEmitter(entry.getKey(), emitter);
            }
          }
        }
      }
    }
  }

  private void removeEmitter(String topic, SseEmitter emitter) {
    List<SseEmitter> canalEmitters = emitters.get(topic);
    if (canalEmitters != null) {
      canalEmitters.remove(emitter);
    }
  }
}