package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.PedidoItemKds;
import com.easyorder.api.backend.service.PedidoItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PedidoItemController {

  private final PedidoItemService pedidoItemService;

  @GetMapping("/admin/comandas/kds/{nombreZonaTrabajo}")
  public List<PedidoItemKds> getPedidoItemKds(@PathVariable String nombreZonaTrabajo) {
    return pedidoItemService.obtenerComandasParaKds(nombreZonaTrabajo);
  }

}
