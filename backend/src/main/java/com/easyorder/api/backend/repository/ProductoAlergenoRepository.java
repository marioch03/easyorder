package com.easyorder.api.backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.ProductoAlergeno;
import com.easyorder.api.backend.model.ProductoAlergenoId;

public interface ProductoAlergenoRepository extends JpaRepository<ProductoAlergeno, ProductoAlergenoId> {

  @EntityGraph(attributePaths = { "alergeno" })
  List<ProductoAlergeno> findByProducto_Id(Long idProducto);

  @EntityGraph(attributePaths = { "alergeno" })
  List<ProductoAlergeno> findByProducto_IdIn(Collection<Long> idsProducto);

  void deleteByProducto_Id(Long idProducto);
}