package com.easyorder.api.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = { "sesion", "sesion.mesa", "estado" })
    @Override
    List<Pedido> findAll();

    @EntityGraph(attributePaths = { "sesion", "sesion.mesa", "estado" })
    List<Pedido> findBySesionId(Long sesionId);

}