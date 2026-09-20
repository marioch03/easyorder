package com.easyorder.api.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.easyorder.api.backend.dto.CrearPedidoDTO;
import com.easyorder.api.backend.dto.CrearPedidoItemDTO;
import com.easyorder.api.backend.dto.CuentaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.ProductoNoDisponibleException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Modificador;
import com.easyorder.api.backend.model.Pedido;
import com.easyorder.api.backend.model.PedidoEstado;
import com.easyorder.api.backend.model.PedidoItem;
import com.easyorder.api.backend.model.PedidoItem_Modificador;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.ZonaTrabajo;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.ModificadorRepository;
import com.easyorder.api.backend.repository.PedidoEstadoRepository;
import com.easyorder.api.backend.repository.PedidoItemRepository;
import com.easyorder.api.backend.repository.PedidoRepository;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.SesionEstadoRepository;
import com.easyorder.api.backend.repository.SesionRepository;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoEstadoRepository pedidoEstadoRepository;

    @Mock
    private PedidoItemRepository pedidoItemRepository;

    @Mock
    private ModificadorRepository modificadorRepository;

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private SesionEstadoRepository sesionEstadoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PedidoService pedidoService;

    private Mesa mesa;
    private Sesion sesion;
    private PedidoEstado estadoPendiente;
    private PedidoEstado estadoServido;
    private PedidoEstado estadoListo;
    private PedidoEstado estadoParcial;
    private Producto producto;
    private Modificador modificador;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);

        mesa = new Mesa();
        mesa.setId(1L);
        mesa.setNumero(5);

        sesion = new Sesion();
        sesion.setId(10L);
        sesion.setQrCodeUrl("session-code-123");
        sesion.setMesa(mesa);

        estadoPendiente = new PedidoEstado("PENDIENTE", "Pedido en cola");
        estadoPendiente.setId(1L);

        estadoParcial = new PedidoEstado("PARCIAL", "Parte lista");
        estadoParcial.setId(2L);

        estadoListo = new PedidoEstado("LISTO", "Listo para servir");
        estadoListo.setId(3L);

        estadoServido = new PedidoEstado("SERVIDO", "Servido en mesa");
        estadoServido.setId(4L);

        ZonaTrabajo cocina = new ZonaTrabajo("COCINA");
        ProductoTipo tipo = new ProductoTipo("Comida", "Desc");
        tipo.setZonaTrabajo(cocina);

        producto = new Producto();
        producto.setId(100L);
        producto.setNombre("Hamburguesa");
        producto.setPrecio(new BigDecimal("10.00"));
        producto.setDisponible(true);
        producto.setTipo(tipo);

        modificador = new Modificador();
        modificador.setId(200L);
        modificador.setNombre("Extra Queso");
        modificador.setPrecioExtra(new BigDecimal("1.50"));
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("Tests para crearPedido")
    class CrearPedidoTests {

        @Test
        @DisplayName("Debe crear el pedido correctamente calculando total con modificadores y publicando SSE")
        void crearPedido_datosValidos_creaPedidoYNotificaSse() {
            CrearPedidoItemDTO itemDTO = new CrearPedidoItemDTO(
                    null, 100L, "Hamburguesa", 2, new BigDecimal("10.00"), "Sin cebolla", false, false, List.of(200L));
            // Precio: (10.00 + 1.50) * 2 = 23.00
            CrearPedidoDTO pedidoDTO = new CrearPedidoDTO(List.of(itemDTO), new BigDecimal("23.00"));

            when(pedidoEstadoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estadoPendiente));
            when(modificadorRepository.findAllById(Set.of(200L))).thenReturn(List.of(modificador));
            when(productoRepository.findAllById(Set.of(100L))).thenReturn(List.of(producto));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> {
                Pedido p = i.getArgument(0);
                p.setId(1L);
                return p;
            });

            PedidoDTO resultado = pedidoService.crearPedido(pedidoDTO, sesion);

            assertThat(resultado).isNotNull();
            assertThat(resultado.idPedido()).isEqualTo(1L);
            assertThat(resultado.nombreEstado()).isEqualTo("PENDIENTE");
            assertThat(resultado.numeroMesa()).isEqualTo(5);

            verify(pedidoRepository).save(any(Pedido.class));
            verify(pedidoItemRepository).saveAll(any());
            verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar ProductoNoDisponibleException si un producto no está disponible")
        void crearPedido_productoNoDisponible_lanzaExcepcion() {
            producto.setDisponible(false);
            CrearPedidoItemDTO itemDTO = new CrearPedidoItemDTO(
                    null, 100L, "Hamburguesa", 1, new BigDecimal("10.00"), null, false, false, List.of());
            CrearPedidoDTO pedidoDTO = new CrearPedidoDTO(List.of(itemDTO), new BigDecimal("10.00"));

            when(pedidoEstadoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estadoPendiente));
            when(productoRepository.findAllById(Set.of(100L))).thenReturn(List.of(producto));

            assertThatThrownBy(() -> pedidoService.crearPedido(pedidoDTO, sesion))
                    .isInstanceOf(ProductoNoDisponibleException.class)
                    .hasMessageContaining("Los siguientes productos ya no están disponibles: Hamburguesa");
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException si el total enviado no coincide con el calculado")
        void crearPedido_totalDiscrepante_lanzaExcepcion() {
            CrearPedidoItemDTO itemDTO = new CrearPedidoItemDTO(
                    null, 100L, "Hamburguesa", 1, new BigDecimal("10.00"), null, false, false, List.of());
            CrearPedidoDTO pedidoDTO = new CrearPedidoDTO(List.of(itemDTO), new BigDecimal("5.00")); // Enviado 5.00 vs Real 10.00

            when(pedidoEstadoRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estadoPendiente));
            when(productoRepository.findAllById(Set.of(100L))).thenReturn(List.of(producto));

            assertThatThrownBy(() -> pedidoService.crearPedido(pedidoDTO, sesion))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no coincide con el total real calculado");
        }
    }

    @Nested
    @DisplayName("Tests para recalcularEstadoPedido")
    class RecalcularEstadoPedidoTests {

        @Test
        @DisplayName("Debe cambiar a LISTO si todos los items están listos para servir")
        void recalcularEstado_todosListos_cambiaAListo() {
            Pedido pedido = new Pedido();
            pedido.setId(1L);
            pedido.setEstado(estadoPendiente);

            PedidoItem item1 = new PedidoItem();
            item1.setListoParaServir(true);
            PedidoItem item2 = new PedidoItem();
            item2.setListoParaServir(true);

            pedido.setItems(Set.of(item1, item2));

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoEstadoRepository.findByNombre("LISTO")).thenReturn(Optional.of(estadoListo));

            pedidoService.recalcularEstadoPedido(1L);

            assertThat(pedido.getEstado()).isEqualTo(estadoListo);
            verify(pedidoRepository).save(pedido);
            verify(eventPublisher).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe cambiar a PARCIAL si solo algunos items están listos")
        void recalcularEstado_parcial_cambiaAParcial() {
            Pedido pedido = new Pedido();
            pedido.setId(1L);
            pedido.setEstado(estadoPendiente);

            PedidoItem item1 = new PedidoItem();
            item1.setListoParaServir(true);
            PedidoItem item2 = new PedidoItem();
            item2.setListoParaServir(false);

            pedido.setItems(Set.of(item1, item2));

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoEstadoRepository.findByNombre("PARCIAL")).thenReturn(Optional.of(estadoParcial));

            pedidoService.recalcularEstadoPedido(1L);

            assertThat(pedido.getEstado()).isEqualTo(estadoParcial);
            verify(pedidoRepository).save(pedido);
        }
    }

    @Nested
    @DisplayName("Tests para marcarPedidoServido")
    class MarcarPedidoServidoTests {

        @Test
        @DisplayName("Debe marcar todos los items como servidos y listos y actualizar el pedido a SERVIDO")
        void marcarPedidoServido_pedidoValido_actualizaATodosServidos() {
            Pedido pedido = new Pedido();
            pedido.setId(1L);
            pedido.setEstado(estadoListo);

            PedidoItem item = new PedidoItem();
            item.setListoParaServir(true);
            item.setServido(false);
            pedido.setItems(Set.of(item));

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoEstadoRepository.findByNombre("SERVIDO")).thenReturn(Optional.of(estadoServido));

            pedidoService.marcarPedidoServido(1L);

            assertThat(item.isServido()).isTrue();
            assertThat(item.isListoParaServir()).isTrue();
            assertThat(pedido.getEstado()).isEqualTo(estadoServido);

            verify(pedidoRepository).save(pedido);
            verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(any(SseTopicEvent.class));
        }
    }

    @Nested
    @DisplayName("Tests para obtenerCuentaCliente y obtenerCuentaAdmin")
    class CuentaTests {

        @Test
        @DisplayName("Debe calcular y agrupar correctamente los items de la cuenta y su total")
        void obtenerCuentaCliente_conPedidos_agrupaYCalculaTotal() {
            Pedido pedido = new Pedido();
            pedido.setId(1L);
            pedido.setSesion(sesion);

            PedidoItem item1 = new PedidoItem();
            item1.setId(10L);
            item1.setProducto(producto);
            item1.setCantidad(2);
            item1.setPrecioUnitario(new BigDecimal("10.00"));
            item1.setNota("Sin sal");

            PedidoItem_Modificador pim = PedidoItem_Modificador.builder()
                    .modificador(modificador)
                    .precioAplicado(new BigDecimal("1.50"))
                    .build();
            item1.setModificadores(List.of(pim));

            when(sesionRepository.findByQrCodeUrl("session-code-123")).thenReturn(Optional.of(sesion));
            when(pedidoRepository.findBySesionId(10L)).thenReturn(List.of(pedido));
            when(pedidoItemRepository.findByPedidoIdInWithModificadores(List.of(1L))).thenReturn(List.of(item1));

            CuentaDTO cuenta = pedidoService.obtenerCuentaCliente("session-code-123");

            assertThat(cuenta).isNotNull();
            assertThat(cuenta.items()).hasSize(1);
            assertThat(cuenta.items().get(0).cantidad()).isEqualTo(2);
            assertThat(cuenta.total()).isEqualTo(new BigDecimal("23.00"));
        }
    }
}
