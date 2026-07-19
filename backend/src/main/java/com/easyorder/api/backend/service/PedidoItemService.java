package com.easyorder.api.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.PedidoItemKds;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.PedidoItem;
import com.easyorder.api.backend.repository.PedidoItemRepository;
import com.easyorder.api.backend.repository.ZonaTrabajoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoItemService {

  private final PedidoItemRepository pedidoItemRepository;
  private final ZonaTrabajoRepository zonaTrabajoRepository;

  public List<PedidoItemKds> obtenerComandasParaKds(String nombreZonaTrabajo) {

    if (!zonaTrabajoRepository.existsByNombreIgnoreCase(nombreZonaTrabajo.toUpperCase())) {
      throw new NoEncontradoException("ZonaTRabajo no encontrada: " + nombreZonaTrabajo);
    }

    List<PedidoItem> items = pedidoItemRepository.findPendientesByZona(nombreZonaTrabajo.toUpperCase());

    return items.stream()
        .map(item -> new PedidoItemKds(
            item.getId(),
            item.getProducto().getNombre(),
            item.getCantidad(),
            item.getNota(),
            item.getPedido().getSesion().getMesa().getNumero(),
            item.isListoParaServir()))
        .toList();
  }

  public PedidoItem marcarPedidoItemListo(Long id) {
    PedidoItem pedidoItem = pedidoItemRepository.findById(id)
        .orElseThrow(() -> new NoEncontradoException(
            "PedidoItem no encontrado para el ID: " + id));
    pedidoItem.setListoParaServir(true);
    return pedidoItemRepository.save(pedidoItem);
  }

}
