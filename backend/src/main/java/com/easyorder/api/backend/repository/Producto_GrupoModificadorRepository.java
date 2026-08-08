package com.easyorder.api.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.easyorder.api.backend.model.Producto_GrupoModificador;
import com.easyorder.api.backend.model.Producto_GrupoModificadorId;

public interface Producto_GrupoModificadorRepository
    extends JpaRepository<Producto_GrupoModificador, Producto_GrupoModificadorId> {

  @Query("SELECT DISTINCT pgm FROM Producto_GrupoModificador pgm " +
      "JOIN FETCH pgm.grupo g " +
      "LEFT JOIN FETCH g.modificadores " +
      "WHERE pgm.producto.id IN :productoIds " +
      "ORDER BY pgm.ordenVisual ASC")
  List<Producto_GrupoModificador> findByProducto_IdIn(List<Long> productoIds);

  List<Producto_GrupoModificador> findByProducto_IdInOrderByOrdenVisualAsc(List<Long> productoIds);

  List<Producto_GrupoModificador> findByProducto_IdOrderByOrdenVisualAsc(Long productoId);
}