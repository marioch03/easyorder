package com.easyorder.api.backend.dto;

import java.io.Serializable;
import java.util.List;

public record SseTicketData(Long tenantId, String username, List<String> roles) implements Serializable {
}
