package com.easyorder.api.backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.easyorder.api.backend.model.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    List<PedidoItem> findByPedidoId(Long id);

    @Query("""
                SELECT item FROM PedidoItem item
                JOIN FETCH item.pedido p
                JOIN FETCH p.sesion s
                JOIN FETCH s.mesa m
                JOIN FETCH item.producto prod
                WHERE item.listoParaServir = false
                AND item.zonaTrabajo.nombre = :zonaTrabajoNombre
                ORDER BY p.createdAt ASC
            """)
    List<PedidoItem> findPendientesByZona(@Param("zonaTrabajoNombre") String zonaTrabajoNombre);

    List<PedidoItem> findByPedidoIdIn(Collection<Long> pedidoIds);

}