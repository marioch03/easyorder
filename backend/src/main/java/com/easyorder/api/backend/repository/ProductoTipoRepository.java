package com.easyorder.api.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.model.ZonaTrabajo;

public interface ProductoTipoRepository extends JpaRepository<ProductoTipo, Long> {

    ProductoTipo findByNombre(String nombre);

    List<ProductoTipo> findByZonaTrabajo(ZonaTrabajo zonaTrabajo);
}
