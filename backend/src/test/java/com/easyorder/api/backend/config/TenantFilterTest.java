package com.easyorder.api.backend.config;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.easyorder.api.backend.model.Tenant;
import com.easyorder.api.backend.repository.TenantRepository;
import com.easyorder.api.backend.service.JwtService;
import com.easyorder.api.backend.tenant.TenantContext;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantFilter tenantFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Debe extraer tenantId del token JWT en cabecera Authorization")
    void doFilter_conBearerToken_extraeTenantId() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid_jwt_token");
        when(jwtService.extractTenantId("valid_jwt_token")).thenReturn(42L);

        // Subclass filter chain to verify TenantContext is active DURING filterChain.doFilter
        MockFilterChain inspectingChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                assertThat(TenantContext.getOrNull()).isEqualTo(42L);
            }
        };

        tenantFilter.doFilter(request, response, inspectingChain);

        // After filter execution, TenantContext must be cleared
        assertThat(TenantContext.getOrNull()).isNull();
        verify(jwtService).extractTenantId("valid_jwt_token");
    }

    @Test
    @DisplayName("Debe extraer tenant del header X-Tenant-Slug cuando no hay token JWT")
    void doFilter_conHeaderTenantSlug_extraeTenant() throws ServletException, IOException {
        request.addHeader("X-Tenant-Slug", "bar-central");
        Tenant tenant = new Tenant("Bar Central", "bar-central");
        tenant.setId(7L);

        when(tenantRepository.findBySlugAndActivoTrue("bar-central")).thenReturn(Optional.of(tenant));

        MockFilterChain inspectingChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                assertThat(TenantContext.getOrNull()).isEqualTo(7L);
            }
        };

        tenantFilter.doFilter(request, response, inspectingChain);

        assertThat(TenantContext.getOrNull()).isNull();
        verify(tenantRepository).findBySlugAndActivoTrue("bar-central");
    }

    @Test
    @DisplayName("Debe continuar la cadena sin fijar tenant si no se proporcionan cabeceras")
    void doFilter_sinCabeceras_continuaCadena() throws ServletException, IOException {
        MockFilterChain inspectingChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                assertThat(TenantContext.getOrNull()).isNull();
            }
        };

        tenantFilter.doFilter(request, response, inspectingChain);

        assertThat(TenantContext.getOrNull()).isNull();
    }
}
