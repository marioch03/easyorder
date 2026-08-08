package com.easyorder.api.backend.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.dto.ModificadorKdsDTO;
import com.easyorder.api.backend.dto.PedidoItemKds;
import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.event.SseTopicEvent;
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

  private final PedidoService pedidoService;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public List<PedidoItemKds> obtenerComandasParaKds(String nombreZonaTrabajo) {

    if (!zonaTrabajoRepository.existsByNombreIgnoreCase(nombreZonaTrabajo.toUpperCase())) {
      throw new NoEncontradoException("Zona de Trabajo no encontrada: " + nombreZonaTrabajo);
    }

    List<PedidoItem> items = pedidoItemRepository.findPendientesByZona(nombreZonaTrabajo.toUpperCase());

    return items.stream()
        .map(item -> {

          List<ModificadorKdsDTO> modificadoresDTO = (item.getModificadores() == null)
              ? List.of()
              : item.getModificadores().stream()
                  .map(mod -> new ModificadorKdsDTO(
                      mod.getId(),
                      mod.getModificador().getNombre()))
                  .toList();

          return new PedidoItemKds(
              item.getId(),
              item.getProducto().getNombre(),
              item.getProducto().getTipo().getId(),
              item.getCantidad(),
              item.getNota(),
              item.getPedido().getSesion().getMesa().getNumero(),
              item.isListoParaServir(),
              item.getPedido().getCreatedAt(),
              modificadoresDTO);
        })
        .toList();
  }

  @Transactional
  public void marcarPedidoItemListo(Long id) {
    PedidoItem pedidoItem = pedidoItemRepository.findById(id)
        .orElseThrow(() -> new NoEncontradoException(
            "PedidoItem no encontrado para el ID: " + id));
    pedidoItem.setListoParaServir(true);
    pedidoItemRepository.save(pedidoItem);
    pedidoService.recalcularEstadoPedido(pedidoItem.getPedido().getId());
    eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.PEDIDOS));
    eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.KDS));
  }

  @Transactional
  public void marcarPedidoItemServido(Long id) {
    PedidoItem pedidoItem = pedidoItemRepository.findById(id)
        .orElseThrow(() -> new NoEncontradoException(
            "PedidoItem no encontrado para el ID: " + id));
    pedidoItem.setServido(true);
    pedidoItemRepository.save(pedidoItem);
    eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.PEDIDOS));
    eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.KDS));
  }

}
