package com.mario.tfg.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mario.tfg.backend.dto.ProductoDTO;
import com.mario.tfg.backend.exception.NoEncontradoException;
import com.mario.tfg.backend.model.Producto;
import com.mario.tfg.backend.model.ProductoTipo;
import com.mario.tfg.backend.repository.ProductoRepository;
import com.mario.tfg.backend.repository.ProductoTipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    private final ProductoTipoRepository productoTipoRepository;

    public List<ProductoDTO> findAll(String sessionCode) {

        return productoRepository.findAll().stream()
                .map(producto -> new ProductoDTO(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio().doubleValue(),
                        producto.isDisponible(),
                        producto.getImagen(),
                        producto.getTipo().getId()))
                .toList();
    }

    public Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Producto no encontrado. ID: " + id));
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> findByNombreContainingIgnoreCase(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<ProductoTipo> getTipos(String sessionCode) {
        return productoTipoRepository.findAll();
    }
}
