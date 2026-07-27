package com.easyorder.api.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Mesa;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    @EntityGraph(attributePaths = { "estado", "zona" })
    @Override
    List<Mesa> findAll();

    Optional<Mesa> findByNumero(int numero);

    boolean existsByNumero(int numero);

    void deleteByNumero(int numero);
}
