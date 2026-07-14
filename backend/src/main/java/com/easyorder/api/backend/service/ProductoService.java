package com.easyorder.api.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.ProductoTipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    private final ProductoTipoRepository productoTipoRepository;

    @Cacheable(value = "productos", key = "'todos'")
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(producto -> new ProductoDTO(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio().doubleValue(),
                        producto.isDisponible(),
                        producto.getImagen(),
                        producto.getTipo().getId()))
                .collect(Collectors.toList());
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

    @Cacheable(value = "producto_tipos", key = "'todos'")
    public List<ProductoTipoDTO> getTipos() {
        return productoTipoRepository.findAll().stream()
                .map(tipo -> new ProductoTipoDTO(tipo.getId(), tipo.getNombre()))
                .collect(Collectors.toList());
    }
}
