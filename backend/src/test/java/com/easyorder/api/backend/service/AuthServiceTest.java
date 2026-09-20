package com.easyorder.api.backend.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.easyorder.api.backend.dto.LoginRequest;
import com.easyorder.api.backend.dto.RegisterRequest;
import com.easyorder.api.backend.dto.TokenResponse;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.TokenInvalidoException;
import com.easyorder.api.backend.model.RefreshToken;
import com.easyorder.api.backend.model.Tenant;
import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.model.UsuarioRol;
import com.easyorder.api.backend.repository.RefreshTokenRepository;
import com.easyorder.api.backend.repository.TenantRepository;
import com.easyorder.api.backend.repository.UsuarioRepository;
import com.easyorder.api.backend.repository.UsuarioRolRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Tenant tenant;
    private UsuarioRol rolAdmin;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setSlug("bar-central");
        tenant.setActivo(true);

        rolAdmin = new UsuarioRol("ADMIN", "Admin");
        rolAdmin.setId(1L);

        usuario = Usuario.builder()
                .id(10L)
                .nombre("admin")
                .clave("hashed_password")
                .rol(rolAdmin)
                .tenantId(1L)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Tests para register")
    class RegisterTests {

        @Test
        @DisplayName("Debe registrar un nuevo usuario y generar tokens")
        void register_datosValidos_retornaTokens() {
            // RegisterRequest(String nombre, String rol, String clave, String tenantSlug)
            RegisterRequest request = new RegisterRequest("nuevo_admin", "ADMIN", "password123", "bar-central");

            when(tenantRepository.findIdBySlug("bar-central")).thenReturn(Optional.of(1L));
            when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
            when(usuarioRolRepository.findByNombre("ADMIN")).thenReturn(Optional.of(rolAdmin));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            when(jwtService.generateAccessToken(any(Usuario.class))).thenReturn("access_token_jwt");

            TokenResponse response = authService.register(request);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access_token_jwt");
            assertThat(response.refreshToken()).isNotNull();

            verify(usuarioRepository).save(any(Usuario.class));
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si el tenant no existe")
        void register_tenantInexistente_lanzaExcepcion() {
            RegisterRequest request = new RegisterRequest("user", "ADMIN", "pass", "bar-fantasma");

            when(tenantRepository.findIdBySlug("bar-fantasma")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Bar no encontrado o inactivo");
        }
    }

    @Nested
    @DisplayName("Tests para login")
    class LoginTests {

        @Test
        @DisplayName("Debe autenticar correctamente y retornar tokens")
        void login_credencialesValidas_retornaTokens() {
            // LoginRequest(String nombre, String clave, String tenantSlug)
            LoginRequest request = new LoginRequest("admin", "password123", "bar-central");

            when(tenantRepository.findBySlugAndActivoTrue("bar-central")).thenReturn(Optional.of(tenant));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken("admin", "password123"));
            when(usuarioRepository.findByNombre("admin")).thenReturn(Optional.of(usuario));
            when(jwtService.generateAccessToken(usuario)).thenReturn("access_token_jwt");

            TokenResponse response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access_token_jwt");
            assertThat(response.refreshToken()).isNotNull();

            verify(refreshTokenRepository).revocarTodosLosTokensDelUsuario(10L);
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si el tenant no existe o no está activo")
        void login_tenantInvalido_lanzaExcepcion() {
            LoginRequest request = new LoginRequest("admin", "password123", "bar-inactivo");

            when(tenantRepository.findBySlugAndActivoTrue("bar-inactivo")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Bar no encontrado o inactivo");
        }

        @Test
        @DisplayName("Debe lanzar BadCredentialsException si la autenticación falla")
        void login_credencialesIncorrectas_lanzaExcepcion() {
            LoginRequest request = new LoginRequest("admin", "wrong_pass", "bar-central");

            when(tenantRepository.findBySlugAndActivoTrue("bar-central")).thenReturn(Optional.of(tenant));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Credenciales incorrectas");
        }
    }

    @Nested
    @DisplayName("Tests para refreshToken")
    class RefreshTokenTests {

        @Test
        @DisplayName("Debe refrescar el access token cuando el refresh token es válido")
        void refreshToken_tokenValido_retornaNuevoToken() {
            RefreshToken rt = RefreshToken.builder()
                    .token("valid_refresh_token")
                    .usuario(usuario)
                    .tenantId(1L)
                    .expiresAt(Instant.now().plus(5, ChronoUnit.DAYS))
                    .revoked(false)
                    .build();

            when(refreshTokenRepository.findByToken("valid_refresh_token")).thenReturn(Optional.of(rt));
            when(jwtService.generateAccessToken(usuario)).thenReturn("new_access_token_jwt");

            TokenResponse response = authService.refreshToken("valid_refresh_token");

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("new_access_token_jwt");
            assertThat(response.refreshToken()).isEqualTo("valid_refresh_token");
        }

        @Test
        @DisplayName("Debe lanzar TokenInvalidoException si el token es nulo o vacío")
        void refreshToken_tokenNuloOVacio_lanzaExcepcion() {
            assertThatThrownBy(() -> authService.refreshToken(null))
                    .isInstanceOf(TokenInvalidoException.class)
                    .hasMessageContaining("Refresh token no proporcionado");

            assertThatThrownBy(() -> authService.refreshToken(""))
                    .isInstanceOf(TokenInvalidoException.class)
                    .hasMessageContaining("Refresh token no proporcionado");
        }

        @Test
        @DisplayName("Debe lanzar TokenInvalidoException si el token ha sido revocado")
        void refreshToken_tokenRevocado_lanzaExcepcion() {
            RefreshToken rt = RefreshToken.builder()
                    .token("revoked_token")
                    .usuario(usuario)
                    .tenantId(1L)
                    .expiresAt(Instant.now().plus(5, ChronoUnit.DAYS))
                    .revoked(true)
                    .build();

            when(refreshTokenRepository.findByToken("revoked_token")).thenReturn(Optional.of(rt));

            assertThatThrownBy(() -> authService.refreshToken("revoked_token"))
                    .isInstanceOf(TokenInvalidoException.class)
                    .hasMessageContaining("El token ha sido revocado");
        }

        @Test
        @DisplayName("Debe lanzar TokenInvalidoException si el token ha expirado")
        void refreshToken_tokenExpirado_lanzaExcepcion() {
            RefreshToken rt = RefreshToken.builder()
                    .token("expired_token")
                    .usuario(usuario)
                    .tenantId(1L)
                    .expiresAt(Instant.now().minus(1, ChronoUnit.DAYS))
                    .revoked(false)
                    .build();

            when(refreshTokenRepository.findByToken("expired_token")).thenReturn(Optional.of(rt));

            assertThatThrownBy(() -> authService.refreshToken("expired_token"))
                    .isInstanceOf(TokenInvalidoException.class)
                    .hasMessageContaining("El token ha expirado");

            verify(refreshTokenRepository).revocarToken("expired_token");
        }
    }

    @Nested
    @DisplayName("Tests para logout")
    class LogoutTests {

        @Test
        @DisplayName("Debe revocar el token si se proporciona uno válido")
        void logout_conToken_revocaToken() {
            authService.logout("token_a_revocar");

            verify(refreshTokenRepository).revocarToken("token_a_revocar");
        }
    }
}
