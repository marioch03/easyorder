package com.easyorder.api.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.ProductoTipo;

public interface ProductoTipoRepository extends JpaRepository<ProductoTipo, Long> {

    ProductoTipo findByNombre(String nombre);
}
