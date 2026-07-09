package com.mario.tfg.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mario.tfg.backend.dto.LoginRequest;
import com.mario.tfg.backend.dto.RegisterRequest;
import com.mario.tfg.backend.dto.TokenResponse;
import com.mario.tfg.backend.exception.TokenInvalidoException;
import com.mario.tfg.backend.model.Token;
import com.mario.tfg.backend.model.Usuario;
import com.mario.tfg.backend.model.UsuarioRol;
import com.mario.tfg.backend.repository.TokenRepository;
import com.mario.tfg.backend.repository.UsuarioRepository;
import com.mario.tfg.backend.repository.UsuarioRolRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public TokenResponse register(RegisterRequest request) {
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .clave(passwordEncoder.encode(request.clave()))
                .createdAt(LocalDateTime.now())
                .rol(getRolByNombre(request.rol()))
                .activo(true)
                .build();
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        String jwtToken = jwtService.generateToken(usuarioGuardado);
        String refreshToken = jwtService.generateRefreshToken(usuarioGuardado);
        saveUserToken(usuarioGuardado, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request) {
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
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenInvalidoException("Token de refresco no proporcionado");
        }

        String nombre = jwtService.extractNombre(refreshToken);
        if (nombre == null) {
            throw new TokenInvalidoException("Token invalido");
        }
        Usuario usuario = usuarioRepository.findByNombre(nombre).orElseThrow();
        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new TokenInvalidoException("Token invalido");
        }
        String jwtToken = jwtService.generateToken(usuario);
        revokeAllUserTokens(usuario);
        saveUserToken(usuario, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
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
