package com.easyorder.api.backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.LoginRequest;
import com.easyorder.api.backend.dto.RegisterRequest;
import com.easyorder.api.backend.dto.TokenResponse;
import com.easyorder.api.backend.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        TokenResponse entity = authService.register(request);
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse token = authService.login(request);
        setRefreshTokenCookie(response, token.refreshToken(), 7 * 24 * 60 * 60);
        return ResponseEntity.ok(new TokenResponse(token.accessToken(), null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        TokenResponse tokenResponse = authService.refreshToken(refreshToken);

        setRefreshTokenCookie(response, tokenResponse.refreshToken(), 7 * 24 * 60 * 60);

        return ResponseEntity.ok(new TokenResponse(tokenResponse.accessToken(), null));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        setRefreshTokenCookie(response, "", 0);

        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String value, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(false) // ⚠️ Cambiar a true en producción (HTTPS)
                .path("/api/v1/auth") // Restringido solo a los endpoints de autenticación
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
