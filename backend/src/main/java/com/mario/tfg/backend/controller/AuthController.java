package com.mario.tfg.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mario.tfg.backend.dto.LoginRequest;
import com.mario.tfg.backend.dto.RefreshTokenRequest;
import com.mario.tfg.backend.dto.RegisterRequest;
import com.mario.tfg.backend.dto.TokenResponse;
import com.mario.tfg.backend.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        TokenResponse entity = authService.register(request);
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request.refreshToken());
    }
}
