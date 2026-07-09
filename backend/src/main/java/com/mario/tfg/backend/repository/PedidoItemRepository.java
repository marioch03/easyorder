package com.mario.tfg.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    List<PedidoItem> findByPedidoId(Long id);
}