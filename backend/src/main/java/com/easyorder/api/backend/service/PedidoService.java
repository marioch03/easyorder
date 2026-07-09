package com.easyorder.api.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.CrearPedidoDTO;
import com.easyorder.api.backend.dto.CuentaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;
import com.easyorder.api.backend.dto.PedidoItemDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Pedido;
import com.easyorder.api.backend.model.PedidoEstado;
import com.easyorder.api.backend.model.PedidoItem;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.PedidoEstadoRepository;
import com.easyorder.api.backend.repository.PedidoItemRepository;
import com.easyorder.api.backend.repository.PedidoRepository;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.SesionEstadoRepository;
import com.easyorder.api.backend.repository.SesionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
        private final ProductoRepository productoRepository;
        private final PedidoRepository pedidoRepository;
        private final PedidoEstadoRepository pedidoEstadoRepository;
        private final PedidoItemRepository pedidoItemRepository;
        private final SesionRepository sesionRepository;
        private final SesionEstadoRepository sesionEstadoRepository;
        private final MesaRepository mesaRepository;

        private final WebSocketService webSocketService;

        @Transactional
        public Pedido crearPedido(CrearPedidoDTO dto, String sessionCode) {
                Sesion sesion = sesionRepository.findByQrCodeUrl(sessionCode)
                                .orElseThrow(() -> new NoEncontradoException(
                                                "Sesión no encontrada para el QR code: " + sessionCode));

                PedidoEstado estadoPendiente = pedidoEstadoRepository.findByNombre("PENDIENTE")
                                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado"));

                Pedido nuevoPedido = new Pedido(sesion, estadoPendiente);

                final Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

                List<PedidoItem> productos = dto.items().stream()
                                .map(pedidoItemDTO -> {
                                        Producto producto = productoRepository.findById(pedidoItemDTO.idProducto())
                                                        .orElseThrow(() -> new NoEncontradoException(
                                                                        "Producto no encontrado. Id: "
                                                                                        + pedidoItemDTO.idProducto()));

                                        PedidoItem pedidoItem = new PedidoItem();
                                        pedidoItem.setPedido(pedidoGuardado);
                                        pedidoItem.setProducto(producto);
                                        pedidoItem.setCantidad(pedidoItemDTO.cantidad());
                                        pedidoItem.setPrecioUnitario(producto.getPrecio());
                                        pedidoItem.setNota(pedidoItemDTO.nota());
                                        pedidoItem.setListoParaServir(false);

                                        return pedidoItem;
                                })
                                .toList();
                pedidoItemRepository.saveAll(productos);
                notificarCambios();
                return pedidoGuardado;
        }

        public List<PedidoDTO> listarPedidos() {
                return pedidoRepository.findAll().stream()
                                .map(pedido -> new PedidoDTO(
                                                pedido.getId(),
                                                pedido.getSesion().getId(),
                                                pedido.getSesion().getMesa().getNumero(),
                                                pedido.getEstado().getNombre(),
                                                pedido.getCreatedAt(),
                                                getPedidoItems(pedido.getId())))
                                .toList();
        }

        public void notificarCambios() {
                List<PedidoDTO> pedidos = listarPedidos();
                webSocketService.notifyPedidosStateChange(pedidos);
        }

        public Pedido getPedido(Long id) {
                return pedidoRepository.findById(id)
                                .orElseThrow(() -> new NoEncontradoException("Pedido no encontrado. Id: " + id));
        }

        public List<PedidoItemDTO> getPedidoItems(Long id) {
                Pedido pedido = getPedido(id);
                List<PedidoItem> items = pedidoItemRepository.findByPedidoId(pedido.getId());

                return items.stream()
                                .map(item -> new PedidoItemDTO(
                                                item.getProducto().getId(),
                                                item.getProducto().getNombre(),
                                                item.getCantidad(),
                                                item.getPrecioUnitario(),
                                                item.getNota()))
                                .toList();
        }

        public Pedido save(Pedido pedido) {
                return pedidoRepository.save(pedido);
        }

        public Pedido cambiarEstado(Long id, String estado) {
                Pedido pedido = pedidoRepository.findById(id)
                                .orElseThrow(() -> new NoEncontradoException("Pedido no encontrado. Id: " + id));

                PedidoEstado nuevoEstado = pedidoEstadoRepository.findByNombre(estado.toUpperCase())
                                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: " + estado));

                pedido.setEstado(nuevoEstado);
                pedido = pedidoRepository.save(pedido);
                notificarCambios();
                return pedido;
        }

        public List<PedidoDTO> listarPedidosPorSesion(String sessionCode) {
                Sesion sesion = sesionRepository.findByQrCodeUrl(sessionCode)
                                .orElseThrow(() -> new NoEncontradoException(
                                                "Sesión no encontrada para el QR code: " + sessionCode));
                return pedidoRepository.findBySesionId(sesion.getId()).stream()
                                .map(pedido -> new PedidoDTO(
                                                pedido.getId(),
                                                pedido.getSesion().getId(),
                                                pedido.getSesion().getMesa().getNumero(),
                                                pedido.getEstado().getNombre(),
                                                pedido.getCreatedAt(),
                                                getPedidoItems(pedido.getId())))
                                .toList();
        }

        public CuentaDTO obtenerCuenta(Long idMesa) {
                Mesa mesa = mesaRepository.findById(idMesa)
                                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + idMesa));
                SesionDTO sesion = obtenerSesionActiva(mesa);
                List<Pedido> pedidos = pedidoRepository.findBySesionId(sesion.getId());

                List<PedidoItemDTO> itemsAgrupados = pedidos.stream()
                                .flatMap(pedido -> pedidoItemRepository.findByPedidoId(pedido.getId()).stream())
                                .collect(Collectors.collectingAndThen(
                                                Collectors.toMap(
                                                                item -> item.getProducto().getId(),
                                                                item -> new PedidoItemDTO(
                                                                                item.getProducto().getId(),
                                                                                item.getProducto().getNombre(),
                                                                                item.getCantidad(),
                                                                                item.getPrecioUnitario(),
                                                                                item.getNota()),
                                                                (existente, nuevo) -> new PedidoItemDTO(
                                                                                existente.idProducto(),
                                                                                existente.nombreProducto(),
                                                                                existente.cantidad() + nuevo.cantidad(),
                                                                                existente.precioUnitario(),
                                                                                existente.nota())),
                                                map -> new ArrayList<>(map.values())));

                BigDecimal total = itemsAgrupados.stream()
                                .map(i -> i.precioUnitario().multiply(BigDecimal.valueOf(i.cantidad())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new CuentaDTO(itemsAgrupados, total);
        }

        public CuentaDTO obtenerCuentaCliente(String sessionCode) {
                Mesa mesa = sesionRepository.findByQrCodeUrl(sessionCode)
                                .orElseThrow(() -> new NoEncontradoException(
                                                "Sesión no encontrada para el QR code: " + sessionCode))
                                .getMesa();
                return obtenerCuenta(mesa.getId());
        }

        private SesionDTO obtenerSesionActiva(Mesa mesa) {
                SesionEstado estadoActiva = sesionEstadoRepository.findByNombre("ACTIVA")
                                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: ACTIVA"));
                Optional<Sesion> sesionMesa = sesionRepository.findByMesaAndEstado(mesa, estadoActiva);
                if (sesionMesa.isPresent()) {
                        Sesion sesion = sesionMesa.get();
                        SesionDTO sesionDTO = new SesionDTO();
                        sesionDTO.setId(sesion.getId());
                        sesionDTO.setQrCodeUrl(sesion.getQrCodeUrl());
                        return sesionDTO;
                }
                return null;
        }

}
