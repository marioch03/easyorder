package com.easyorder.api.backend.tenant;

import org.springframework.core.task.TaskDecorator;

/**
 * Propaga el tenant del hilo llamador (el que atiende la petición HTTP) al
 * hilo que realmente ejecuta una tarea @Async. Necesario porque
 * TenantContext usa un ThreadLocal simple, no InheritableThreadLocal —
 * ver el javadoc de TenantContext para el motivo. El valor se captura AQUÍ,
 * en decorate(), que se ejecuta en el hilo llamador en el momento en que se
 * encola la tarea; el resto del Runnable corre en un hilo del pool.
 */
public class TenantAwareTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    Long tenantId = TenantContext.getOrNull();
    return () -> {
      try {
        if (tenantId != null) {
          TenantContext.set(tenantId);
        }
        runnable.run();
      } finally {
        TenantContext.clear();
      }
    };
  }
}