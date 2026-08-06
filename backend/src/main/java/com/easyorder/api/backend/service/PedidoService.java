package com.easyorder.api.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.dto.CrearPedidoDTO;
import com.easyorder.api.backend.dto.CuentaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;
import com.easyorder.api.backend.dto.PedidoEstadoEnum;
import com.easyorder.api.backend.dto.PedidoItemDTO;
import com.easyorder.api.backend.dto.SseTopic;
import com.easyorder.api.backend.event.SseTopicEvent;
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
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public PedidoDTO crearPedido(CrearPedidoDTO dto, Sesion sesion) {
		PedidoEstado estadoPendiente = pedidoEstadoRepository.findByNombre(PedidoEstadoEnum.PENDIENTE.getValue())
				.orElseThrow(() -> new NoEncontradoException("Estado no encontrado"));

		Pedido nuevoPedido = new Pedido(sesion, estadoPendiente);
		final Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

		Set<Long> productoIds = dto.items().stream()
				.map(item -> item.idProducto())
				.collect(Collectors.toSet());

		Map<Long, Producto> productoMap = productoRepository.findAllById(productoIds).stream()
				.collect(Collectors.toMap(Producto::getId, Function.identity()));

		List<PedidoItem> items = dto.items().stream().map(itemDTO -> {
			Producto producto = productoMap.get(itemDTO.idProducto());
			if (producto == null) {
				throw new NoEncontradoException("Producto no encontrado. Id: " + itemDTO.idProducto());
			}

			PedidoItem pedidoItem = new PedidoItem();
			pedidoItem.setPedido(pedidoGuardado);
			pedidoItem.setProducto(producto);
			pedidoItem.setCantidad(itemDTO.cantidad());
			pedidoItem.setPrecioUnitario(producto.getPrecio());
			pedidoItem.setNota(itemDTO.nota());
			pedidoItem.setListoParaServir(false);
			pedidoItem.setZonaTrabajo(producto.getTipo().getZonaTrabajo());
			return pedidoItem;
		}).toList();

		pedidoItemRepository.saveAll(items);

		eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.PEDIDOS));
		eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.KDS));

		return new PedidoDTO(
				pedidoGuardado.getId(),
				pedidoGuardado.getSesion().getId(),
				pedidoGuardado.getSesion().getMesa().getNumero(),
				pedidoGuardado.getCreatedAt(),
				pedidoGuardado.getEstado().getNombre());
	}

	@Transactional
	public PedidoDTO crearPedidoCliente(CrearPedidoDTO dto, String sessionCode) {
		Sesion sesion = sesionRepository.findByQrCodeUrl(sessionCode)
				.orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para el QR code: " + sessionCode));
		return crearPedido(dto, sesion);
	}

	public PedidoDTO crearPedidoAdmin(CrearPedidoDTO dto, Long idMesa) {
		Sesion sesion = sesionRepository.findByMesaIdAndEstadoNombre(idMesa, "ACTIVA")
				.orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para la mesa: " + idMesa));
		return crearPedido(dto, sesion);
	}

	public List<PedidoDTO> listarPedidos() {
		return construirPedidoDTOs(pedidoRepository.findAll());
	}

	public List<PedidoDTO> listarPedidosPorSesion(String sessionCode) {
		Sesion sesion = sesionRepository.findByQrCodeUrl(sessionCode)
				.orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para el QR code: " + sessionCode));
		return construirPedidoDTOs(pedidoRepository.findBySesionId(sesion.getId()));
	}

	public Pedido getPedido(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException("Pedido no encontrado. Id: " + id));
	}

	@Transactional(readOnly = true)
	public List<PedidoItemDTO> getPedidoItems(Long id) {
		return pedidoItemRepository.findByPedidoId(id).stream()
				.map(this::toPedidoItemDTO)
				.toList();
	}

	public Pedido save(Pedido pedido) {
		return pedidoRepository.save(pedido);
	}

	@Transactional
	public void marcarPedidoServido(Long id) {
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException("Pedido no encontrado. Id: " + id));

		pedido.getItems().forEach(item -> {
			item.setListoParaServir(true);
			item.setServido(true);
		});

		PedidoEstado estadoServido = pedidoEstadoRepository.findByNombre(PedidoEstadoEnum.SERVIDO.getValue())
				.orElseThrow(() -> new NoEncontradoException("Estado no encontrado: SERVIDO"));
		pedido.setEstado(estadoServido);

		pedidoRepository.save(pedido);
		eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.PEDIDOS));
	}

	@Transactional
	public void recalcularEstadoPedido(Long idPedido) {
		Pedido pedido = pedidoRepository.findById(idPedido)
				.orElseThrow(() -> new NoEncontradoException("Pedido no encontrado: " + idPedido));

		Set<PedidoItem> items = pedido.getItems();
		if (items == null || items.isEmpty()) {
			return;
		}

		boolean todosListos = items.stream().allMatch(PedidoItem::isListoParaServir);
		boolean ningunoListo = items.stream().noneMatch(PedidoItem::isListoParaServir);

		PedidoEstadoEnum enumObjetivo = todosListos
				? PedidoEstadoEnum.LISTO
				: ningunoListo ? PedidoEstadoEnum.PENDIENTE : PedidoEstadoEnum.PARCIAL;

		if (pedido.getEstado() != null && enumObjetivo.getValue().equals(pedido.getEstado().getNombre())) {
			return;
		}

		PedidoEstado nuevoEstado = pedidoEstadoRepository.findByNombre(enumObjetivo.getValue())
				.orElseThrow(() -> new NoEncontradoException("PedidoEstado no encontrado: " + enumObjetivo.getValue()));

		pedido.setEstado(nuevoEstado);
		pedidoRepository.save(pedido);
		eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.PEDIDOS));
	}

	public CuentaDTO obtenerCuenta(Long idMesa) {
		Mesa mesa = mesaRepository.findById(idMesa)
				.orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + idMesa));

		Sesion sesion = obtenerSesionActivaEntidad(mesa);
		if (sesion == null) {
			return new CuentaDTO(List.of(), BigDecimal.ZERO);
		}

		return construirCuenta(sesion.getId());
	}

	public CuentaDTO obtenerCuentaCliente(String sessionCode) {
		Sesion sesion = sesionRepository.findByQrCodeUrl(sessionCode)
				.orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para el QR code: " + sessionCode));
		return construirCuenta(sesion.getId());
	}

	@Transactional(readOnly = true)
	private List<PedidoDTO> construirPedidoDTOs(List<Pedido> pedidos) {
		if (pedidos.isEmpty()) {
			return List.of();
		}

		List<Long> pedidoIds = pedidos.stream()
				.map(Pedido::getId)
				.toList();

		Map<Long, List<PedidoItemDTO>> itemsPorPedido = pedidoItemRepository.findByPedidoIdIn(pedidoIds).stream()
				.collect(Collectors.groupingBy(
						item -> item.getPedido().getId(),
						Collectors.mapping(this::toPedidoItemDTO, Collectors.toList())));

		return pedidos.stream()
				.map(pedido -> new PedidoDTO(
						pedido.getId(),
						pedido.getSesion().getId(),
						pedido.getSesion().getMesa().getNumero(),
						pedido.getEstado().getNombre(),
						pedido.getCreatedAt(),
						itemsPorPedido.getOrDefault(pedido.getId(), List.of())))
				.toList();
	}

	@Transactional(readOnly = true)
	private CuentaDTO construirCuenta(Long idSesion) {
		List<Pedido> pedidos = pedidoRepository.findBySesionId(idSesion);
		List<Long> pedidoIds = pedidos.stream()
				.map(Pedido::getId)
				.toList();

		List<PedidoItem> items = pedidoIds.isEmpty()
				? List.of()
				: pedidoItemRepository.findByPedidoIdIn(pedidoIds);

		List<PedidoItemDTO> itemsAgrupados = agruparPorProductoYNota(items);

		BigDecimal total = itemsAgrupados.stream()
				.map(i -> i.precioUnitario().multiply(BigDecimal.valueOf(i.cantidad())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new CuentaDTO(itemsAgrupados, total);
	}

	private List<PedidoItemDTO> agruparPorProductoYNota(List<PedidoItem> items) {
		Map<String, PedidoItemDTO> agrupados = new LinkedHashMap<>();

		for (PedidoItem item : items) {
			String nota = item.getNota() == null ? "" : item.getNota();
			String clave = item.getProducto().getId() + "|" + nota;

			agrupados.merge(clave, toPedidoItemDTO(item), (existente, nuevo) -> new PedidoItemDTO(
					null,
					existente.idProducto(),
					existente.nombreProducto(),
					existente.cantidad() + nuevo.cantidad(),
					existente.precioUnitario(),
					existente.nota(),
					existente.listoParaServir() && nuevo.listoParaServir(),
					existente.servido() && nuevo.servido()));
		}

		return new ArrayList<>(agrupados.values());
	}

	private Sesion obtenerSesionActivaEntidad(Mesa mesa) {
		SesionEstado estadoActiva = sesionEstadoRepository.findByNombre("ACTIVA")
				.orElseThrow(() -> new NoEncontradoException("Estado no encontrado: ACTIVA"));
		return sesionRepository.findByMesaAndEstado(mesa, estadoActiva).orElse(null);
	}

	private PedidoItemDTO toPedidoItemDTO(PedidoItem item) {
		return new PedidoItemDTO(
				item.getId(),
				item.getProducto().getId(),
				item.getProducto().getNombre(),
				item.getCantidad(),
				item.getPrecioUnitario(),
				item.getNota(),
				item.isListoParaServir(),
				item.isServido());
	}
}