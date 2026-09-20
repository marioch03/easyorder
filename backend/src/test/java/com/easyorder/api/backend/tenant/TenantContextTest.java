package com.easyorder.api.backend.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("set y get deben almacenar y recuperar el tenantId en el ThreadLocal")
    void setYGet_retornaTenantIdCorrecto() {
        TenantContext.set(100L);

        assertThat(TenantContext.get()).isEqualTo(100L);
        assertThat(TenantContext.getOrNull()).isEqualTo(100L);
    }

    @Test
    @DisplayName("get debe lanzar IllegalStateException si no hay tenant configurado")
    void get_sinTenant_lanzaExcepcion() {
        assertThatThrownBy(TenantContext::get)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay tenant en el contexto");
    }

    @Test
    @DisplayName("getOrNull debe retornar null si no hay tenant configurado")
    void getOrNull_sinTenant_retornaNull() {
        assertThat(TenantContext.getOrNull()).isNull();
    }

    @Test
    @DisplayName("clear debe eliminar el tenantId del ThreadLocal")
    void clear_eliminaTenantDelContexto() {
        TenantContext.set(50L);
        assertThat(TenantContext.getOrNull()).isEqualTo(50L);

        TenantContext.clear();
        assertThat(TenantContext.getOrNull()).isNull();
    }
}
