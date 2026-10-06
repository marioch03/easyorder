package com.easyorder.api.backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.easyorder.api.backend.dto.CrearProductoDTO;
import com.easyorder.api.backend.dto.EditarProductoDTO;
import com.easyorder.api.backend.dto.ProductoComandaDTO;
import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/cliente/productos")
    public List<ProductoDTO> getProductos(
            @RequestHeader("X-Session-Code") String sessionCode) {

        return productoService.findAll();
    }

    @GetMapping("/admin/productos/{id}")
    public ResponseEntity<ProductoDTO> getProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoDTO(id));
    }

    @PostMapping("/admin/productos")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody CrearProductoDTO dto) {
        ProductoDTO nuevo = productoService.crearProducto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevo.getId())
                .toUri();
        return ResponseEntity.created(location).body(nuevo);
    }

    @PutMapping("/admin/productos/{id}")
    public ResponseEntity<EditarProductoDTO> editarProducto(
            @PathVariable Long id,
            @Valid @RequestBody EditarProductoDTO dto) {

        return ResponseEntity.ok(
                productoService.editarProducto(id, dto));
    }

    @DeleteMapping("/admin/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/productos/tipos")
    public List<ProductoTipoDTO> getTipos(@RequestHeader("X-Session-Code") String sessionCode) {
        return productoService.getTipos();
    }

    @GetMapping("/admin/productos/tipos")
    public List<ProductoTipoDTO> getTipos() {
        return productoService.getTipos();
    }

    @GetMapping("/admin/productos")
    public List<ProductoDTO> getProductos() {

        return productoService.findAll();
    }

    @GetMapping("/admin/productos/simplified")
    public List<ProductoComandaDTO> getProductosSimplificados() {
        return productoService.getProductosSimplificados();
    }

    @GetMapping("/admin/productos/tipos/{nombreZonaTrabajo}")
    public List<ProductoTipoDTO> getTiposKds(@PathVariable String nombreZonaTrabajo) {
        return productoService.getTiposKds(nombreZonaTrabajo);
    }

}