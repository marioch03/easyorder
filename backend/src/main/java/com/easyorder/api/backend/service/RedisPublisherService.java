package com.easyorder.api.backend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.config.RedisPubSubConfig;
import com.easyorder.api.backend.dto.RedisNotificationMessage;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisherService {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public void publish(SseTopicEvent event) {

    try {
      RedisNotificationMessage message = new RedisNotificationMessage(
          event.tenantId(),
          event.topic().getValue());

      String jsonMessage = objectMapper.writeValueAsString(message);

      redisTemplate.convertAndSend(
          RedisPubSubConfig.SSE_CHANNEL,
          jsonMessage);

      log.debug(
          "Evento SSE publicado. tenantId={}, topic={}",
          event.tenantId(),
          event.topic().getValue());

    } catch (JsonProcessingException e) {

      log.error(
          "Error serializando evento SSE. tenantId={}, topic={}",
          event.tenantId(),
          event.topic().getValue(),
          e);
    }
  }
}