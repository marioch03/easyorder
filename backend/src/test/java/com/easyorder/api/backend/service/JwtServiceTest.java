package com.easyorder.api.backend.service;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.model.UsuarioRol;
import com.easyorder.api.backend.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Generate a valid 256-bit Base64 secret
        byte[] secretBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            secretBytes[i] = (byte) (i + 1);
        }
        String base64Secret = Base64.getEncoder().encodeToString(secretBytes);

        jwtService = new JwtService("EasyOrderIssuer", base64Secret, 3600000L, refreshTokenRepository);

        UsuarioRol rol = new UsuarioRol("ADMIN", "Admin");
        usuario = Usuario.builder()
                .id(1L)
                .nombre("admin_user")
                .clave("secret")
                .rol(rol)
                .tenantId(42L)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe generar un token JWT válido y permitir extraer el subject y tenantId")
    void generateAccessToken_generaTokenValido() {
        String token = jwtService.generateAccessToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.getSubject(token)).isEqualTo("admin_user");
        assertThat(jwtService.extractTenantId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("isValid debe retornar false para un token corrupto")
    void isValid_tokenInvalido_retornaFalse() {
        assertThat(jwtService.isValid("token.invalido.falso")).isFalse();
    }

    @Test
    @DisplayName("isValidAndNotRevoked debe retornar false si el token está revocado")
    void isValidAndNotRevoked_tokenRevocado_retornaFalse() {
        String token = jwtService.generateAccessToken(usuario);
        String tokenId = jwtService.getTokenId(token);

        when(refreshTokenRepository.isTokenRevoked(tokenId)).thenReturn(true);

        assertThat(jwtService.isValidAndNotRevoked(token)).isFalse();
    }

    @Test
    @DisplayName("isValidAndNotRevoked debe retornar true si el token no está revocado")
    void isValidAndNotRevoked_tokenNoRevocado_retornaTrue() {
        String token = jwtService.generateAccessToken(usuario);
        String tokenId = jwtService.getTokenId(token);

        when(refreshTokenRepository.isTokenRevoked(tokenId)).thenReturn(false);

        assertThat(jwtService.isValidAndNotRevoked(token)).isTrue();
    }
}
