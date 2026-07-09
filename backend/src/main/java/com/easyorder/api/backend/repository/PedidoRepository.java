package com.easyorder.api.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findBySesionId(Long idSesion);
}