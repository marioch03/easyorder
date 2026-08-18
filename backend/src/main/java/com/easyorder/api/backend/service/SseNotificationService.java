package com.easyorder.api.backend.service;

import java.io.IOException;
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

  private static final long EMITTER_TIMEOUT = 3_600_000L;

  private static final String EVENT_REFRESH = "refresh";
  private static final String DATA_REFRESH = "refresh";

  private final Map<String, List<SseEmitter>> topicEmitters = new ConcurrentHashMap<>();

  private String buildKey(Long tenantId, String topic) {
    return tenantId + ":" + topic;
  }

  public SseEmitter suscribir(Long tenantId, String topic) {

    String channelKey = buildKey(tenantId, topic);

    log.info(
        "Cliente conectado al canal SSE local: [{}]",
        channelKey);

    SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);

    topicEmitters
        .computeIfAbsent(
            channelKey,
            key -> new CopyOnWriteArrayList<>())
        .add(emitter);

    emitter.onCompletion(
        () -> removeEmitter(channelKey, emitter));

    emitter.onTimeout(
        () -> removeEmitter(channelKey, emitter));

    emitter.onError(
        error -> removeEmitter(channelKey, emitter));

    try {

      emitter.send(
          SseEmitter.event()
              .name("connected")
              .data("Conexión establecida con éxito"));

    } catch (IOException | IllegalStateException e) {

      log.debug(
          "Error durante handshake SSE. Canal [{}]",
          channelKey,
          e);

      removeEmitter(channelKey, emitter);
    }

    return emitter;
  }

  public void notificarLocales(
      Long tenantId,
      String topic) {

    String channelKey = buildKey(tenantId, topic);

    List<SseEmitter> emitters = topicEmitters.get(channelKey);

    if (emitters == null || emitters.isEmpty()) {

      log.debug(
          "Sin clientes SSE locales para [{}]",
          channelKey);

      return;
    }

    log.debug(
        "Enviando refresh SSE. canal={}, clientes={}",
        channelKey,
        emitters.size());

    for (SseEmitter emitter : emitters) {

      try {

        emitter.send(
            SseEmitter.event()
                .name(EVENT_REFRESH)
                .data(DATA_REFRESH));

      } catch (IOException | IllegalStateException e) {

        log.debug(
            "Cliente SSE desconectado. Canal [{}]",
            channelKey);

        removeEmitter(
            channelKey,
            emitter);
      }
    }
  }

  @Scheduled(fixedRate = 15_000)
  public void sendHeartbeat() {

    topicEmitters.forEach(
        (channelKey, emitters) -> {

          for (SseEmitter emitter : emitters) {

            try {

              emitter.send(
                  SseEmitter.event()
                      .comment("ping"));

            } catch (
                IOException | IllegalStateException e) {

              removeEmitter(
                  channelKey,
                  emitter);
            }
          }
        });
  }

  private void removeEmitter(
      String channelKey,
      SseEmitter emitter) {

    topicEmitters.computeIfPresent(
        channelKey,
        (key, emitters) -> {

          emitters.remove(emitter);

          return emitters.isEmpty()
              ? null
              : emitters;
        });
  }
}