package com.easyorder.api.backend.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.repository.RefreshTokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    private final String issuer;
    private final SecretKey key;
    private final long jwtExpiration;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(@Value("${jwt.issuer}") String issuer, @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpiration,
            RefreshTokenRepository refreshTokenRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessToken(Usuario usuario) {
        return buildToken(usuario, jwtExpiration);
    }

    private String buildToken(Usuario usuario, long expiration) {
        Instant now = Instant.now();
        Instant expirationInstant = now.plusMillis(expiration);
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .id(jti)
                .subject(usuario.getNombre())
                .issuer(issuer)
                .claim("roles", usuario.getRol().getNombre())
                .claim("tenantId", usuario.getTenantId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationInstant))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public Long extractTenantId(String token) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload().get("tenantId", Long.class);
        } catch (Exception e) {
            log.error("Error al extraer el tenantId del token: {}", e.getMessage());
            return null;
        }
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getTokenId(String token) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload().getId();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenRevoked(String token) {
        String tokenId = getTokenId(token);
        boolean revoked = tokenId != null && refreshTokenRepository.isTokenRevoked(tokenId);
        log.debug("RefreshToken revocation check - ID: {}, Revoked: {}", tokenId, revoked);
        return revoked;
    }

    public boolean isValidAndNotRevoked(String token) {
        return isValid(token) && !isTokenRevoked(token);
    }

    public Timestamp getExpiration(String token) {
        try {
            Date expiration = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload().getExpiration();
            return new Timestamp(expiration.getTime());
        } catch (Exception e) {
            return null;
        }
    }

}
