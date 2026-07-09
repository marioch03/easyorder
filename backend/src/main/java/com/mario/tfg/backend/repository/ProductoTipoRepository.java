package com.mario.tfg.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.ProductoTipo;

public interface ProductoTipoRepository extends JpaRepository<ProductoTipo, Long> {

    ProductoTipo findByNombre(String nombre);
}
