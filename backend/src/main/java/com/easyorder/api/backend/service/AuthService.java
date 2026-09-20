package com.easyorder.api.backend.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.easyorder.api.backend.tenant.TenantContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TenantRepository tenantRepository;
    private final JwtService jwtService;

    public TokenResponse register(RegisterRequest request) {
        Long tenantId = tenantRepository.findIdBySlug(request.tenantSlug())
                .orElseThrow(() -> new NoEncontradoException("Bar no encontrado o inactivo: " + request.tenantSlug()));

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .clave(passwordEncoder.encode(request.clave()))
                .rol(getRolByNombre(request.rol()))
                .activo(true)
                .tenantId(tenantId)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String jwtToken = jwtService.generateAccessToken(usuarioGuardado);
        String refreshToken = createAndSaveRefreshToken(usuarioGuardado);

        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request) {
        Tenant tenant = tenantRepository.findBySlugAndActivoTrue(request.tenantSlug())
                .orElseThrow(() -> new NoEncontradoException("Bar no encontrado o inactivo: " + request.tenantSlug()));

        TenantContext.set(tenant.getId());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.nombre(), request.clave()));

            Usuario usuario = usuarioRepository.findByNombre(request.nombre())
                    .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + request.nombre()));

            String jwtToken = jwtService.generateAccessToken(usuario);

            refreshTokenRepository.revocarTodosLosTokensDelUsuario(usuario.getId());

            String refreshToken = createAndSaveRefreshToken(usuario);

            return new TokenResponse(jwtToken, refreshToken);
        } finally {
            TenantContext.clear();
        }
    }

    public TokenResponse refreshToken(String refreshTokenString) {
        if (refreshTokenString == null || refreshTokenString.isEmpty()) {
            throw new TokenInvalidoException("Refresh token no proporcionado");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new TokenInvalidoException("Refresh token no encontrado"));

        if (refreshToken.isRevoked()) {
            throw new TokenInvalidoException("El token ha sido revocado. Inicia sesión nuevamente.");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.revocarToken(refreshTokenString); // Lo revocamos para limpiar
            throw new TokenInvalidoException("El token ha expirado. Inicia sesión nuevamente.");
        }

        Usuario usuario = refreshToken.getUsuario();
        TenantContext.set(usuario.getTenantId());

        try {
            String newJwtToken = jwtService.generateAccessToken(usuario);

            return new TokenResponse(newJwtToken, refreshTokenString);
        } finally {
            TenantContext.clear();
        }
    }

    public void logout(String refreshTokenString) {
        if (refreshTokenString != null && !refreshTokenString.isEmpty()) {
            refreshTokenRepository.revocarToken(refreshTokenString);
        }
    }

    private UsuarioRol getRolByNombre(String nombre) {
        return usuarioRolRepository.findByNombre(nombre)
                .orElseThrow(() -> new NoEncontradoException("Rol no encontrado: " + nombre));
    }

    private String createAndSaveRefreshToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .tenantId(usuario.getTenantId())
                .token(token)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }
}