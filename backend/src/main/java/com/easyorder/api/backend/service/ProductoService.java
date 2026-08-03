package com.easyorder.api.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.AlergenoDTO;
import com.easyorder.api.backend.dto.EditarProductoDTO;
import com.easyorder.api.backend.dto.ProductoComandaDTO;
import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.model.ZonaTrabajo;
import com.easyorder.api.backend.repository.ProductoAlergenoRepository;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.ProductoTipoRepository;
import com.easyorder.api.backend.repository.ZonaTrabajoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    private final ProductoTipoRepository productoTipoRepository;

    private final ZonaTrabajoRepository zonaTrabajoRepository;

    private final ProductoAlergenoRepository productoAlergenoRepository;

    @Cacheable(value = "productos", key = "'todos'")
    public List<ProductoDTO> findAll() {
        List<Producto> productos = productoRepository.findAll();

        List<Long> productoIds = productos.stream().map(Producto::getId).toList();

        Map<Long, List<AlergenoDTO>> alergenosPorProducto = productoAlergenoRepository
                .findByProducto_IdIn(productoIds).stream()
                .collect(Collectors.groupingBy(
                        pa -> pa.getProducto().getId(),
                        Collectors.mapping(
                                pa -> new AlergenoDTO(pa.getAlergeno().getNombre(), pa.getTipo()),
                                Collectors.toList())));

        return productos.stream()
                .map(producto -> new ProductoDTO(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getPrecio().doubleValue(),
                        producto.isDisponible(),
                        producto.getImagen(),
                        producto.getTipo().getId(),
                        alergenosPorProducto.getOrDefault(producto.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "productos", key = "'simplificados'")
    public List<ProductoComandaDTO> getProductosSimplificados() {
        return productoRepository.findAll().stream()
                .map(producto -> new ProductoComandaDTO(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getPrecio(),
                        producto.getTipo().getId(),
                        producto.isDisponible()))
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

    @CacheEvict(value = "productos", allEntries = true)
    public Producto editarProducto(Long id, EditarProductoDTO dto) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException(
                        "Producto no encontrado. ID: " + id));

        ProductoTipo tipo = productoTipoRepository.findById(dto.tipoId())
                .orElseThrow(() -> new NoEncontradoException(
                        "ProductoTipo no encontrado. ID: " + id));

        producto.setId(dto.id());
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(BigDecimal.valueOf(dto.precio()));
        producto.setDisponible(dto.activo());
        producto.setTipo(tipo);

        return productoRepository.save(producto);
    }

    public List<ProductoTipoDTO> getTiposKds(String nombreZonaTrabajo) {
        ZonaTrabajo zonaTrabajo = zonaTrabajoRepository.findByNombre(nombreZonaTrabajo)
                .orElseThrow(() -> new NoEncontradoException(nombreZonaTrabajo));

        return productoTipoRepository.findByZonaTrabajo(zonaTrabajo).stream()
                .map(tipo -> new ProductoTipoDTO(tipo.getId(), tipo.getNombre()))
                .collect(Collectors.toList());
    }
}
