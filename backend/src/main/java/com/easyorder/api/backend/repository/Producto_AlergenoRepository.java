package com.easyorder.api.backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.ProductoAlergenoId;
import com.easyorder.api.backend.model.Producto_Alergeno;

public interface Producto_AlergenoRepository extends JpaRepository<Producto_Alergeno, ProductoAlergenoId> {

  @EntityGraph(attributePaths = { "alergeno" })
  List<Producto_Alergeno> findByProducto_Id(Long idProducto);

  @EntityGraph(attributePaths = { "alergeno" })
  List<Producto_Alergeno> findByProducto_IdIn(Collection<Long> idsProducto);

  void deleteByProducto_Id(Long idProducto);
}