package com.easyorder.api.backend.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.easyorder.api.backend.dto.SseTopic;

class SseSecurityServiceTest {

    private SseSecurityService sseSecurityService;

    @BeforeEach
    void setUp() {
        sseSecurityService = new SseSecurityService();
    }

    private Authentication crearAuth(String... roles) {
        var authorities = List.of(roles).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new UsernamePasswordAuthenticationToken("testUser", null, authorities);
    }

    @Test
    @DisplayName("CUSTOMER no puede suscribirse a kds, mesas ni pedidos")
    void customer_denegadoEnTodosLosTopics() {
        Authentication auth = crearAuth("ROLE_CUSTOMER");

        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.KDS, auth))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("No dispone de los permisos necesarios para suscribirse al canal SSE 'kds'");

        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.MESAS, auth))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("No dispone de los permisos necesarios para suscribirse al canal SSE 'mesas'");

        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.PEDIDOS, auth))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("No dispone de los permisos necesarios para suscribirse al canal SSE 'pedidos'");
    }

    @Test
    @DisplayName("PERSONAL puede suscribirse a mesas y pedidos, pero no a kds")
    void personal_permisosCorrectos() {
        Authentication auth = crearAuth("PERSONAL");

        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.MESAS, auth))
                .doesNotThrowAnyException();

        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.PEDIDOS, auth))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.KDS, auth))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("COCINA y KDS pueden suscribirse a kds y pedidos, pero no a mesas")
    void cocina_permisosCorrectos() {
        Authentication authCocina = crearAuth("COCINA");
        Authentication authKds = crearAuth("KDS");

        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.KDS, authCocina))
                .doesNotThrowAnyException();
        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.PEDIDOS, authCocina))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.MESAS, authCocina))
                .isInstanceOf(AccessDeniedException.class);

        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.KDS, authKds))
                .doesNotThrowAnyException();
        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.PEDIDOS, authKds))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.MESAS, authKds))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("ADMIN puede suscribirse a todos los topics")
    void admin_accesoTotal() {
        Authentication auth = crearAuth("ADMIN");

        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.KDS, auth))
                .doesNotThrowAnyException();
        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.MESAS, auth))
                .doesNotThrowAnyException();
        assertThatCode(() -> sseSecurityService.validarAccesoTopic(SseTopic.PEDIDOS, auth))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Usuario no autenticado o nulo lanza AccessDeniedException")
    void authNulo_lanzaExcepcion() {
        assertThatThrownBy(() -> sseSecurityService.validarAccesoTopic(SseTopic.PEDIDOS, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("No se encuentra autenticado");
    }
}
