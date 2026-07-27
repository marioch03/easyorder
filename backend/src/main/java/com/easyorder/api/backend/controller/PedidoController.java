package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.CrearPedidoDTO;
import com.easyorder.api.backend.dto.CuentaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;
import com.easyorder.api.backend.dto.PedidoItemDTO;
import com.easyorder.api.backend.model.Pedido;
import com.easyorder.api.backend.service.PedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping("/admin/pedidos/list")
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/admin/pedidos/{id}/items")
    public ResponseEntity<List<PedidoItemDTO>> getPedidoItems(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getPedidoItems(id));
    }

    @GetMapping("/admin/pedidos/{idMesa}/cuenta")
    public ResponseEntity<CuentaDTO> getCuentaAdmin(@PathVariable Long idMesa) {
        return ResponseEntity.ok(pedidoService.obtenerCuenta(idMesa));
    }

    @PostMapping("/admin/pedidos/create/mesa/{idMesa}")
    public ResponseEntity<Pedido> crearPedidoAdmin(
            @Valid @RequestBody CrearPedidoDTO dto,
            @PathVariable Long idMesa) {
        Pedido nuevoPedido = pedidoService.crearPedidoAdmin(dto, idMesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    @PatchMapping("/admin/pedidos/{id}/servido")
    public ResponseEntity<Void> marcarPedidoServido(@PathVariable Long id) {

        pedidoService.marcarPedidoServido(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/pedidos")
    public ResponseEntity<List<PedidoDTO>> getPedidosPorSesion(
            @RequestHeader("X-Session-Code") String sessionCode) {
        return ResponseEntity.ok(pedidoService.listarPedidosPorSesion(sessionCode));
    }

    @GetMapping("/cliente/pedidos/cuenta")
    public ResponseEntity<CuentaDTO> getCuentaCliente(
            @RequestHeader("X-Session-Code") String sessionCode) {
        return ResponseEntity.ok(pedidoService.obtenerCuentaCliente(sessionCode));
    }

    @PostMapping("/cliente/pedidos/create")
    public ResponseEntity<Pedido> crearPedidoCliente(
            @Valid @RequestBody CrearPedidoDTO dto,
            @RequestHeader("X-Session-Code") String sessionCode) {
        Pedido nuevoPedido = pedidoService.crearPedidoCliente(dto, sessionCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }
}