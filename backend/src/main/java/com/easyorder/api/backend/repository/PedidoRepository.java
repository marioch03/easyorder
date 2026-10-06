package com.easyorder.api.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = { "sesion", "sesion.mesa", "estado" })
    @Override
    List<Pedido> findAll();

    @EntityGraph(attributePaths = { "sesion", "sesion.mesa", "estado", "items", "items.producto", "items.modificadores", "items.modificadores.modificador" })
    List<Pedido> findBySesionId(Long sesionId);

    @EntityGraph(attributePaths = { "sesion", "sesion.mesa", "estado", "items", "items.producto" })
    Optional<Pedido> findByIdempotencyKey(String idempotencyKey);

}