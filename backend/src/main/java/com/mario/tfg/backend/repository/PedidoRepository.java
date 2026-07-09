package com.mario.tfg.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findBySesionId(Long idSesion);
}