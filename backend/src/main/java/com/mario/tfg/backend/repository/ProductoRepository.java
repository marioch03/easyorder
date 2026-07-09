package com.mario.tfg.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.Producto;
import com.mario.tfg.backend.model.ProductoTipo;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByTipo(ProductoTipo tipo);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByDisponibleTrue();

    List<Producto> findByDisponibleFalse();
}
