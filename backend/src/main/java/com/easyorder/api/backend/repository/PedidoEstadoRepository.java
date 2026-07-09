package com.easyorder.api.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.PedidoEstado;

public interface PedidoEstadoRepository extends JpaRepository<PedidoEstado, Long> {
    Optional<PedidoEstado> findByNombre(String nombre);

}