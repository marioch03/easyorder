package com.easyorder.api.backend.event;

import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.tenant.TenantContext;

public record SseTopicEvent(SseTopic topic, Long tenantId) {

  public static SseTopicEvent of(SseTopic topic) {
    return new SseTopicEvent(topic, TenantContext.get());
  }
}