package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.EditarProductoDTO;
import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.service.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/cliente/productos/all")
    public List<ProductoDTO> getProductos(
            @RequestHeader("X-Session-Code") String sessionCode) {

        return productoService.findAll();
    }

    @GetMapping("/admin/productos/{id}")
    public Producto getProducto(@PathVariable Long id) {
        return productoService.getProducto(id);

    }

    @PostMapping("/admin/productos/create")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevo = productoService.save(producto);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/admin/productos/edit/{id}")
    public ResponseEntity<Producto> editarProducto(
            @PathVariable Long id,
            @RequestBody EditarProductoDTO dto) {

        return ResponseEntity.ok(
                productoService.editarProducto(id, dto));
    }

    @DeleteMapping("/admin/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoService.getProducto(id) != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/productos/tipos")
    public List<ProductoTipoDTO> getTipos(@RequestHeader("X-Session-Code") String sessionCode) {
        return productoService.getTipos();
    }

    @GetMapping("/admin/productos/tipos")
    public List<ProductoTipoDTO> getTipos() {
        return productoService.getTipos();
    }

    @GetMapping("/admin/productos/all")
    public List<ProductoDTO> getProductos() {

        return productoService.findAll();
    }

}