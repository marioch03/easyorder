package com.easyorder.api.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.dto.AlergenoDTO;
import com.easyorder.api.backend.dto.EditarProductoDTO;
import com.easyorder.api.backend.dto.GrupoModificadorDTO;
import com.easyorder.api.backend.dto.ModificadorDTO;
import com.easyorder.api.backend.dto.ProductoComandaDTO;
import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.GrupoModificador;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.model.ZonaTrabajo;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.ProductoTipoRepository;
import com.easyorder.api.backend.repository.Producto_AlergenoRepository;
import com.easyorder.api.backend.repository.Producto_GrupoModificadorRepository;
import com.easyorder.api.backend.repository.ZonaTrabajoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

	private static final String TENANT_KEY = "T(com.easyorder.api.backend.tenant.TenantContext).get()";

	private final ProductoRepository productoRepository;

	private final ProductoTipoRepository productoTipoRepository;

	private final ZonaTrabajoRepository zonaTrabajoRepository;

	private final Producto_AlergenoRepository productoAlergenoRepository;

	private final Producto_GrupoModificadorRepository producto_GrupoModificadorRepository;

	@Transactional(readOnly = true)
	@Cacheable(value = "productos", key = TENANT_KEY)
	public List<ProductoDTO> findAll() {
		List<Producto> productos = productoRepository.findAll();

		List<Long> productoIds = productos.stream().map(Producto::getId).toList();

		Map<Long, List<AlergenoDTO>> alergenosPorProducto = productoAlergenoRepository
				.findByProducto_IdIn(productoIds).stream()
				.collect(Collectors.groupingBy(
						pa -> pa.getProducto().getId(),
						Collectors.mapping(
								pa -> new AlergenoDTO(pa.getAlergeno().getNombre(), pa.getTipo()),
								Collectors.toList())));

		Map<Long, List<GrupoModificadorDTO>> gruposPorProducto = producto_GrupoModificadorRepository
				.findByProducto_IdIn(productoIds).stream()
				.collect(Collectors.groupingBy(
						pgm -> pgm.getProducto().getId(),
						Collectors.mapping(
								pgm -> mapearAGrupoDTO(pgm.getGrupo()),
								Collectors.toList())));

		return productos.stream()
				.map(producto -> new ProductoDTO(
						producto.getId(),
						producto.getNombre(),
						producto.getDescripcion(),
						producto.getPrecio().doubleValue(),
						producto.isDisponible(),
						producto.getImagen(),
						producto.getTipo().getId(),
						alergenosPorProducto.getOrDefault(producto.getId(), List.of()),
						gruposPorProducto.getOrDefault(producto.getId(), List.of())))
				.collect(Collectors.toList());
	}

	private GrupoModificadorDTO mapearAGrupoDTO(GrupoModificador grupo) {
		List<ModificadorDTO> modificadores = grupo.getModificadores().stream()
				.map(m -> new ModificadorDTO(
						m.getId(),
						m.getNombre(),
						m.getPrecioExtra()))
				.collect(Collectors.toList());

		return new GrupoModificadorDTO(
				grupo.getId(),
				grupo.getNombre(),
				grupo.getSeleccionMinima(),
				grupo.getSeleccionMaxima(),
				modificadores);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "productosSimplificados", key = TENANT_KEY)
	public List<ProductoComandaDTO> getProductosSimplificados() {
		List<Producto> productos = productoRepository.findAll();

		List<Long> productoIds = productos.stream().map(Producto::getId).toList();

		Map<Long, List<GrupoModificadorDTO>> gruposPorProducto = producto_GrupoModificadorRepository
				.findByProducto_IdIn(productoIds).stream()
				.collect(Collectors.groupingBy(
						pgm -> pgm.getProducto().getId(),
						Collectors.mapping(
								pgm -> mapearAGrupoDTO(pgm.getGrupo()),
								Collectors.toList())));

		return productos.stream()
				.map(producto -> new ProductoComandaDTO(
						producto.getId(),
						producto.getNombre(),
						producto.getPrecio(),
						producto.getTipo().getId(),
						producto.isDisponible(),
						gruposPorProducto.getOrDefault(producto.getId(), List.of())))
				.collect(Collectors.toList());
	}

	public Producto getProducto(Long id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException("Producto no encontrado. ID: " + id));
	}

	@CacheEvict(value = { "productos", "productosSimplificados" }, key = TENANT_KEY)
	public Producto save(Producto producto) {
		return productoRepository.save(producto);
	}

	@CacheEvict(value = { "productos", "productosSimplificados" }, key = TENANT_KEY)
	public void deleteById(Long id) {
		productoRepository.deleteById(id);
	}

	public List<Producto> findByNombreContainingIgnoreCase(String nombre) {
		return productoRepository.findByNombreContainingIgnoreCase(nombre);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "productosTipos", key = TENANT_KEY)
	public List<ProductoTipoDTO> getTipos() {
		return productoTipoRepository.findAll().stream()
				.map(tipo -> new ProductoTipoDTO(tipo.getId(), tipo.getNombre()))
				.collect(Collectors.toList());
	}

	@CacheEvict(value = { "productos", "productosSimplificados" }, key = TENANT_KEY)
	public EditarProductoDTO editarProducto(Long id, EditarProductoDTO dto) {

		Producto producto = productoRepository.findById(id)
				.orElseThrow(() -> new NoEncontradoException(
						"Producto no encontrado. ID: " + id));

		ProductoTipo tipo = productoTipoRepository.findById(dto.tipoId())
				.orElseThrow(() -> new NoEncontradoException(
						"ProductoTipo no encontrado. ID: " + dto.tipoId()));

		producto.setNombre(dto.nombre());
		producto.setDescripcion(dto.descripcion());
		producto.setPrecio(BigDecimal.valueOf(dto.precio()));
		producto.setDisponible(dto.disponible());
		producto.setTipo(tipo);

		Producto productoGuardado = productoRepository.save(producto);
		return new EditarProductoDTO(
				productoGuardado.getId(),
				productoGuardado.getNombre(),
				productoGuardado.getDescripcion(),
				productoGuardado.getPrecio().doubleValue(),
				productoGuardado.getTipo().getId(),
				productoGuardado.isDisponible());
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "productoTiposKds", key = TENANT_KEY + " + '::' + #nombreZonaTrabajo")
	public List<ProductoTipoDTO> getTiposKds(String nombreZonaTrabajo) {
		ZonaTrabajo zonaTrabajo = zonaTrabajoRepository.findByNombre(nombreZonaTrabajo)
				.orElseThrow(() -> new NoEncontradoException(nombreZonaTrabajo));

		return productoTipoRepository.findByZonaTrabajo(zonaTrabajo).stream()
				.map(tipo -> new ProductoTipoDTO(tipo.getId(), tipo.getNombre()))
				.collect(Collectors.toList());
	}
}
