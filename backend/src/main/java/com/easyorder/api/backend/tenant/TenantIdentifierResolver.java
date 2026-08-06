package com.easyorder.api.backend.tenant;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long>, HibernatePropertiesCustomizer {

  @Override
  public Long resolveCurrentTenantIdentifier() {
    Long tenantId = TenantContext.getOrNull();
    // System.out.println("----> HIBERNATE PREGUNTA EL TENANT. Valor actual en
    // ThreadLocal: " + tenantId);
    return tenantId != null ? tenantId : -1L;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return true;
  }

  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
  }
}