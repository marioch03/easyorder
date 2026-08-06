package com.easyorder.api.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUsuarioIdAndExpiredFalseAndRevokedFalse(Long id);

    Optional<Token> findByToken(String jwt);

    @Modifying
    @Transactional
    @Query("UPDATE Token t SET t.revoked = true, t.expired = true WHERE t.token = :token")
    void revocarYExpirarToken(@Param("token") String token);

}
