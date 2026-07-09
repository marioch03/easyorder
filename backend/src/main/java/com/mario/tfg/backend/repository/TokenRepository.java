package com.mario.tfg.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllByUsuarioIdAndExpiredFalseAndRevokedFalse(Long id);

    Optional<Token> findByToken(String jwt);

}
