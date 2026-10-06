package com.easyorder.api.backend.ratelimit;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter(rateLimiterService, objectMapper);
    }

    @Test
    @DisplayName("Debe ignorar solicitudes OPTIONS sin invocar al limitador de tasa")
    void doFilter_solicitudOptions_continuaSinRateLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/public/tenant/exists/demo");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimiterService, never()).tryConsume(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Debe ignorar rutas que no pertenecen a la API de negocio (/swagger-ui, /actuator)")
    void doFilter_rutaNoApi_continuaSinRateLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/swagger-ui/index.html");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimiterService, never()).tryConsume(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Debe permitir la solicitud y agregar cabeceras X-RateLimit cuando está dentro del límite")
    void doFilter_dentroDelLimite_agregaCabecerasYPermite() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.50");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiterService.tryConsume("rl:login:192.168.1.50", RateLimitTier.AUTH_LOGIN))
                .thenReturn(new RateLimitResult(true, 5, 4, 60));

        rateLimitFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("5");
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("4");
        assertThat(response.getHeader("X-RateLimit-Reset")).isEqualTo("60");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Debe responder HTTP 429 con Retry-After y cortar la cadena cuando se supera el límite")
    void doFilter_excedeLimite_responde429YBloquea() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/cliente/pedidos");
        request.addHeader("X-Session-Code", "mesa-vip-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiterService.tryConsume("rl:pedido:mesa-vip-123", RateLimitTier.CLIENTE_PEDIDO))
                .thenReturn(new RateLimitResult(false, 10, 0, 42));

        rateLimitFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isEqualTo("42");
        assertThat(response.getHeader("X-RateLimit-Limit")).isEqualTo("10");
        assertThat(response.getHeader("X-RateLimit-Remaining")).isEqualTo("0");
        assertThat(response.getHeader("X-RateLimit-Reset")).isEqualTo("42");

        String content = response.getContentAsString();
        assertThat(content).contains("Too Many Requests");
        assertThat(content).contains("42 segundos");

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Debe extraer la IP original desde la cabecera X-Forwarded-For")
    void doFilter_conProxy_extraeIpDeXForwardedFor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/public/tenant/exists/burger");
        request.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiterService.tryConsume("rl:public:203.0.113.195", RateLimitTier.PUBLIC_DISCOVERY))
                .thenReturn(new RateLimitResult(true, 30, 29, 60));

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimiterService).tryConsume("rl:public:203.0.113.195", RateLimitTier.PUBLIC_DISCOVERY);
        verify(filterChain).doFilter(request, response);
    }
}
