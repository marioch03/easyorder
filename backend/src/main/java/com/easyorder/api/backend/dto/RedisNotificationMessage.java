package com.easyorder.api.backend.dto;

import java.io.Serializable;

public record RedisNotificationMessage(
    Long tenantId,
    String topic) implements Serializable {
}