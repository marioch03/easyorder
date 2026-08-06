package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.PedidoItemKds;
import com.easyorder.api.backend.service.PedidoItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/comandas")
@RequiredArgsConstructor
public class PedidoItemController {

  private final PedidoItemService pedidoItemService;

  @GetMapping("/kds/{nombreZonaTrabajo}")
  public List<PedidoItemKds> getPedidoItemKds(@PathVariable String nombreZonaTrabajo) {
    return pedidoItemService.obtenerComandasParaKds(nombreZonaTrabajo);
  }

  @PatchMapping("/listo/{idPedidoItem}")
  public ResponseEntity<Void> marcarListoItem(@PathVariable Long idPedidoItem) {
    pedidoItemService.marcarPedidoItemListo(idPedidoItem);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/servido/{idPedidoItem}")
  public ResponseEntity<Void> marcaServidoItem(@PathVariable Long idPedidoItem) {
    pedidoItemService.marcarPedidoItemServido(idPedidoItem);
    return ResponseEntity.noContent().build();
  }

}
