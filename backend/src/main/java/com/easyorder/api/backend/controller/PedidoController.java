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

import com.easyorder.api.backend.dto.ActualizarEstadoPedidoDTO;
import com.easyorder.api.backend.dto.CrearPedidoDTO;
import com.easyorder.api.backend.dto.CuentaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;
import com.easyorder.api.backend.dto.PedidoItemDTO;
import com.easyorder.api.backend.model.Pedido;
import com.easyorder.api.backend.service.PedidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping("/admin/pedidos/list")
    public List<PedidoDTO> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @GetMapping("/admin/pedidos/{id}/items")
    public List<PedidoItemDTO> getPedidoItems(@PathVariable Long id) {
        return pedidoService.getPedidoItems(id);
    }

    @PostMapping("/cliente/pedidos/create")
    public ResponseEntity<Pedido> crearPedido(@RequestBody CrearPedidoDTO dto,
            @RequestHeader("X-Session-Code") String sessionCode) {
        Pedido nuevoPedido = pedidoService.crearPedido(dto, sessionCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    @PatchMapping("/admin/pedidos/{id}")
    public ResponseEntity<Pedido> modificarPedido(@PathVariable Long id,
            @RequestBody ActualizarEstadoPedidoDTO nuevoEstado) {
        Pedido pedidoActualizado = pedidoService.cambiarEstado(id, nuevoEstado.nuevoEstado());
        return ResponseEntity.ok(pedidoActualizado);
    }

    @GetMapping("/cliente/pedidos/")
    public List<PedidoDTO> getPedidosPorSesion(@RequestHeader("X-Session-Code") String sessionCode) {
        return pedidoService.listarPedidosPorSesion(sessionCode);
    }

    @GetMapping("/cliente/pedidos/cuenta")
    public CuentaDTO getCuentaCliente(@RequestHeader("X-Session-Code") String sessionCode) {
        return pedidoService.obtenerCuentaCliente(sessionCode);
    }

    @GetMapping("/admin/pedidos/{idMesa}/cuenta")
    public CuentaDTO getCuenta(@PathVariable Long idMesa) {
        return pedidoService.obtenerCuenta(idMesa);
    }
}
