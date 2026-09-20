package com.easyorder.api.backend.tenant;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantIdentifierResolverTest {

    private final TenantIdentifierResolver resolver = new TenantIdentifierResolver();

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("resolveCurrentTenantIdentifier debe retornar el tenantId del TenantContext si existe")
    void resolveCurrentTenantIdentifier_conTenant_retornaTenantId() {
        TenantContext.set(12L);

        Long resolved = resolver.resolveCurrentTenantIdentifier();

        assertThat(resolved).isEqualTo(12L);
    }

    @Test
    @DisplayName("resolveCurrentTenantIdentifier debe retornar -1L si no hay tenant en TenantContext")
    void resolveCurrentTenantIdentifier_sinTenant_retornaMenosUno() {
        Long resolved = resolver.resolveCurrentTenantIdentifier();

        assertThat(resolved).isEqualTo(-1L);
    }

    @Test
    @DisplayName("validateExistingCurrentSessions debe retornar true")
    void validateExistingCurrentSessions_retornaTrue() {
        assertThat(resolver.validateExistingCurrentSessions()).isTrue();
    }

    @Test
    @DisplayName("customize debe registrar el resolver en las propiedades de Hibernate")
    void customize_registraResolver() {
        Map<String, Object> properties = new HashMap<>();

        resolver.customize(properties);

        assertThat(properties).containsKey(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER);
        assertThat(properties.get(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER)).isSameAs(resolver);
    }
}
