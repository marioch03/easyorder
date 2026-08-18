package com.easyorder.api.backend.event;

import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.tenant.TenantContext;

public record SseTopicEvent(
    SseTopic topic,
    Long tenantId) {

  public static SseTopicEvent of(SseTopic topic) {
    Long tenantId = TenantContext.get();

    if (tenantId == null) {
      throw new IllegalStateException(
          "No se puede crear un SseTopicEvent sin tenantId");
    }

    return new SseTopicEvent(topic, tenantId);
  }
}