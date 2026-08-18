package com.easyorder.api.backend.service;

import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.RedisNotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriberService {

	private final ObjectMapper objectMapper;
	private final SseNotificationService sseNotificationService;

	public void handleMessage(String message) {

		try {

			RedisNotificationMessage notification = objectMapper.readValue(
					message,
					RedisNotificationMessage.class);

			sseNotificationService.notificarLocales(
					notification.tenantId(),
					notification.topic());

		} catch (Exception e) {

			log.error(
					"Error procesando mensaje SSE recibido desde Redis",
					e);
		}
	}
}