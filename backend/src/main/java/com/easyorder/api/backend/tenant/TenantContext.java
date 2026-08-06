package com.easyorder.api.backend.tenant;

public class TenantContext {

  private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

  public static void set(Long tenantId) {
    CURRENT_TENANT.set(tenantId);
  }

  public static Long get() {
    Long tenantId = CURRENT_TENANT.get();
    if (tenantId == null) {
      throw new IllegalStateException("No hay tenant en el contexto de la petición actual");
    }
    return tenantId;
  }

  public static Long getOrNull() {
    return CURRENT_TENANT.get();
  }

  public static void clear() {
    CURRENT_TENANT.remove();
  }
}