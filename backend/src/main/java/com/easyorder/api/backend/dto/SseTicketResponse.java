package com.easyorder.api.backend.dto;

public record SseTicketResponse(String ticket, long expiresInSeconds) {
}
