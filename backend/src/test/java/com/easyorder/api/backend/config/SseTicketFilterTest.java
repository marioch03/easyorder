package com.easyorder.api.backend.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.easyorder.api.backend.dto.SseTicketData;
import com.easyorder.api.backend.service.SseTicketService;
import com.easyorder.api.backend.tenant.TenantContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class SseTicketFilterTest {

    @Mock
    private SseTicketService sseTicketService;

    @InjectMocks
    private SseTicketFilter sseTicketFilter;

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Ticket válido en /sse/stream/{topic} establece TenantContext y Authentication y continúa la cadena")
    void doFilterInternal_ticketValido_estableceContextos() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/sse/stream/pedidos");
        request.setParameter("ticket", "test-valid-ticket");
        MockHttpServletResponse response = new MockHttpServletResponse();

        SseTicketData data = new SseTicketData(42L, "camarero1", List.of("PERSONAL"));
        when(sseTicketService.redeemTicket("test-valid-ticket")).thenReturn(Optional.of(data));

        MockFilterChain filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                // Verificar que dentro del filtro, el TenantContext y SecurityContext están activos
                assertThat(TenantContext.get()).isEqualTo(42L);
                var auth = SecurityContextHolder.getContext().getAuthentication();
                assertThat(auth).isNotNull();
                assertThat(auth.getName()).isEqualTo("camarero1");
                assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("PERSONAL"));
            }
        };

        sseTicketFilter.doFilter(request, response, filterChain);

        // Al finalizar la petición, TenantContext debe haberse limpiado
        assertThat(TenantContext.getOrNull()).isNull();
    }

    @Test
    @DisplayName("Ticket inválido o expirado responde HTTP 401 Unauthorized y no continúa la cadena")
    void doFilterInternal_ticketInvalido_retorna401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/sse/stream/pedidos");
        request.setParameter("ticket", "test-invalid-ticket");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(sseTicketService.redeemTicket("test-invalid-ticket")).thenReturn(Optional.empty());

        sseTicketFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains("El ticket SSE no es válido");
        assertThat(TenantContext.getOrNull()).isNull();
    }

    @Test
    @DisplayName("Petición a /sse/stream sin parámetro ticket continúa la cadena sin autenticar por ticket")
    void doFilterInternal_sinTicket_continuaCadena() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/sse/stream/pedidos");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        sseTicketFilter.doFilter(request, response, filterChain);

        verify(sseTicketService, never()).redeemTicket(null);
    }
}
