package com.easyorder.api.backend.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT r FROM RefreshToken r WHERE r.usuario.id = :usuarioId AND r.revoked = false AND r.expiresAt > :now")
    List<RefreshToken> findAllActivosByUsuarioId(@Param("usuarioId") Long usuarioId, @Param("now") Instant now);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.token = :token")
    void revocarToken(@Param("token") String token);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.usuario.id = :usuarioId")
    void revocarTodosLosTokensDelUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM RefreshToken r WHERE r.token = :tokenId AND r.revoked = true")
    boolean isTokenRevoked(@Param("tokenId") String tokenId);
}