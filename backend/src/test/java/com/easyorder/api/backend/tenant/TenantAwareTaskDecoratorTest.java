package com.easyorder.api.backend.tenant;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantAwareTaskDecoratorTest {

    private final TenantAwareTaskDecorator decorator = new TenantAwareTaskDecorator();

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("decorate debe propagar el tenantId del hilo llamador al hilo de ejecución de la tarea")
    void decorate_propagaTenantId() {
        TenantContext.set(99L);

        AtomicReference<Long> capturedTenantInTask = new AtomicReference<>();

        Runnable originalTask = () -> {
            capturedTenantInTask.set(TenantContext.getOrNull());
        };

        Runnable decoratedTask = decorator.decorate(originalTask);

        // Clear current thread context to simulate switching to an async pool thread
        TenantContext.clear();

        // Run decorated task
        decoratedTask.run();

        // Task must have seen tenantId = 99L
        assertThat(capturedTenantInTask.get()).isEqualTo(99L);

        // After decorated task finishes, ThreadLocal must be cleared
        assertThat(TenantContext.getOrNull()).isNull();
    }

    @Test
    @DisplayName("decorate debe funcionar correctamente si no hay tenantId en el hilo llamador")
    void decorate_sinTenant_funcionaSinErrores() {
        AtomicReference<Long> capturedTenantInTask = new AtomicReference<>();

        Runnable originalTask = () -> {
            capturedTenantInTask.set(TenantContext.getOrNull());
        };

        Runnable decoratedTask = decorator.decorate(originalTask);
        decoratedTask.run();

        assertThat(capturedTenantInTask.get()).isNull();
        assertThat(TenantContext.getOrNull()).isNull();
    }
}
