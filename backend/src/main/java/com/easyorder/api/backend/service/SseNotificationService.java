package com.easyorder.api.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
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
    } catch (IOException e) {
      emitter.completeWithError(e);
      removeEmitter(topic, emitter);
    }

    return emitter;
  }

  public void notificar(String topic) {
    List<SseEmitter> canalEmitters = emitters.get(topic);

    if (canalEmitters != null) {
      for (SseEmitter emitter : canalEmitters) {
        try {
          emitter.send(SseEmitter.event().data("refresh"));
        } catch (IOException e) {
          emitter.complete();
          removeEmitter(topic, emitter);
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
