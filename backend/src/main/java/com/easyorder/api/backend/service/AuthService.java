package com.easyorder.api.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.LoginRequest;
import com.easyorder.api.backend.dto.RegisterRequest;
import com.easyorder.api.backend.dto.TokenResponse;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.TokenInvalidoException;
import com.easyorder.api.backend.model.Tenant;
import com.easyorder.api.backend.model.Token;
import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.model.UsuarioRol;
import com.easyorder.api.backend.repository.TenantRepository;
import com.easyorder.api.backend.repository.TokenRepository;
import com.easyorder.api.backend.repository.UsuarioRepository;
import com.easyorder.api.backend.repository.UsuarioRolRepository;
import com.easyorder.api.backend.tenant.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final TokenRepository tokenRepository;
    private final TenantRepository tenantRepository;
    private final JwtService jwtService;

    public TokenResponse register(RegisterRequest request) {
        Long tenantId = tenantRepository.findIdBySlug(request.tenantSlug())
                .orElseThrow(() -> new NoEncontradoException("Bar no encontrado o inactivo" + request.tenantSlug()));
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .clave(passwordEncoder.encode(request.clave()))
                .createdAt(LocalDateTime.now())
                .rol(getRolByNombre(request.rol()))
                .activo(true)
                .tenantId(tenantId)
                .build();
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        String jwtToken = jwtService.generateToken(usuarioGuardado);
        String refreshToken = jwtService.generateRefreshToken(usuarioGuardado);
        saveUserToken(usuarioGuardado, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request) {
        Tenant tenant = tenantRepository.findBySlugAndActivoTrue(request.tenantSlug())
                .orElseThrow(() -> new NoEncontradoException("Bar no encontrado o inactivo" + request.tenantSlug()));
        TenantContext.set(tenant.getId());
        System.out.println("TenantContext: " + TenantContext.get());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.nombre(),
                            request.clave()));

            Usuario usuario = usuarioRepository.findByNombre(request.nombre()).orElseThrow();
            String jwtToken = jwtService.generateToken(usuario);
            String refreshToken = jwtService.generateRefreshToken(usuario);
            revokeAllUserTokens(usuario);
            saveUserToken(usuario, jwtToken);
            return new TokenResponse(jwtToken, refreshToken);
        } finally {
            TenantContext.clear();
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenInvalidoException("Token de refresco no proporcionado");
        }

        String nombre = jwtService.extractNombre(refreshToken);
        if (nombre == null) {
            throw new TokenInvalidoException("Token invalido");
        }

        Long tenantId = jwtService.extractTenantId(refreshToken);
        if (tenantId != null) {
            TenantContext.set(tenantId);
        }

        try {
            Usuario usuario = usuarioRepository.findByNombre(nombre)
                    .orElseThrow(() -> new TokenInvalidoException(
                            "Usuario no encontrado en la base de datos para este tenant"));

            if (!jwtService.isTokenValid(refreshToken, usuario)) {
                throw new TokenInvalidoException("Token invalido");
            }

            String jwtToken = jwtService.generateToken(usuario);
            revokeAllUserTokens(usuario);
            try {
                saveUserToken(usuario, jwtToken);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // Ignoramos silenciosamente la colisión de la petición concurrente del frontend
                System.out.println("Token duplicado ignorado de forma segura.");
            }

            return new TokenResponse(jwtToken, refreshToken);

        } finally {
            TenantContext.clear();
        }
    }

    public UsuarioRol getRolByNombre(String nombre) {
        return usuarioRolRepository.findByNombre(nombre).orElseThrow();
    }

    private void saveUserToken(Usuario usuario, String jwtToken) {
        Token token = Token.builder()
                .usuario(usuario)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Usuario usuario) {
        final List<Token> validUserTokens = tokenRepository
                .findAllByUsuarioIdAndExpiredFalseAndRevokedFalse(usuario.getId());
        if (!validUserTokens.isEmpty()) {
            for (Token token : validUserTokens) {
                token.setRevoked(true);
                token.setExpired(true);
            }
            tokenRepository.saveAll(validUserTokens);
        }
    }
}
