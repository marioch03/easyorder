package com.easyorder.api.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.dto.LoginRequest;
import com.easyorder.api.backend.dto.RegisterRequest;
import com.easyorder.api.backend.dto.TokenResponse;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y el token generado")
        void register_retorna200() throws Exception {
            // RegisterRequest(String nombre, String rol, String clave, String tenantSlug)
            RegisterRequest request = new RegisterRequest("admin", "ADMIN", "password123", "bar-central");
            TokenResponse tokenResponse = new TokenResponse("jwt_access_token", "refresh_token");

            when(authService.register(any(RegisterRequest.class))).thenReturn(tokenResponse);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").value("jwt_access_token"));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200, setear cookie refreshToken y retornar solo accessToken en body")
        void login_credencialesValidas_retorna200YSeteaCookie() throws Exception {
            // LoginRequest(String nombre, String clave, String tenantSlug)
            LoginRequest request = new LoginRequest("admin", "password123", "bar-central");
            TokenResponse tokenResponse = new TokenResponse("jwt_access_token", "sample_refresh_token");

            when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").value("jwt_access_token"))
                    .andExpect(header().exists("Set-Cookie"))
                    .andExpect(cookie().value("refreshToken", "sample_refresh_token"));
        }
    }

    @Nested
    @DisplayName("POST /auth/refresh")
    class RefreshEndpointTests {

        @Test
        @DisplayName("Debe renovar token y retornar HTTP 200 con nueva cookie")
        void refreshToken_cookieValida_retorna200() throws Exception {
            TokenResponse tokenResponse = new TokenResponse("new_jwt_access_token", "refreshed_refresh_token");

            when(authService.refreshToken("sample_refresh_token")).thenReturn(tokenResponse);

            mockMvc.perform(post("/auth/refresh")
                            .cookie(new Cookie("refreshToken", "sample_refresh_token")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").value("new_jwt_access_token"))
                    .andExpect(cookie().value("refreshToken", "refreshed_refresh_token"));
        }
    }

    @Nested
    @DisplayName("POST /auth/logout")
    class LogoutEndpointTests {

        @Test
        @DisplayName("Debe cerrar sesión, borrar cookie y retornar HTTP 200")
        void logout_retorna200YBorraCookie() throws Exception {
            doNothing().when(authService).logout("sample_refresh_token");

            mockMvc.perform(post("/auth/logout")
                            .cookie(new Cookie("refreshToken", "sample_refresh_token")))
                    .andExpect(status().isOk())
                    .andExpect(cookie().maxAge("refreshToken", 0));

            verify(authService).logout("sample_refresh_token");
        }
    }
}
