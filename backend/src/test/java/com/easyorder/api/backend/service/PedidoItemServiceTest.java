package com.easyorder.api.backend.service;

import java.util.List;
import java.util.Optional;

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

import com.easyorder.api.backend.dto.PedidoItemKds;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Modificador;
import com.easyorder.api.backend.model.Pedido;
import com.easyorder.api.backend.model.PedidoItem;
import com.easyorder.api.backend.model.PedidoItem_Modificador;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.repository.PedidoItemRepository;
import com.easyorder.api.backend.repository.ZonaTrabajoRepository;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class PedidoItemServiceTest {

    @Mock
    private PedidoItemRepository pedidoItemRepository;

    @Mock
    private ZonaTrabajoRepository zonaTrabajoRepository;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PedidoItemService pedidoItemService;

    private Pedido pedido;
    private PedidoItem pedidoItem;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);

        Mesa mesa = new Mesa();
        mesa.setNumero(3);

        Sesion sesion = new Sesion();
        sesion.setMesa(mesa);

        pedido = new Pedido();
        pedido.setId(10L);
        pedido.setSesion(sesion);

        ProductoTipo tipo = new ProductoTipo("Bebidas", "Refrescos");
        tipo.setId(5L);

        Producto producto = new Producto();
        producto.setId(100L);
        producto.setNombre("Coca Cola");
        producto.setTipo(tipo);

        Modificador mod = new Modificador();
        mod.setId(1L);
        mod.setNombre("Con Hielo");

        PedidoItem_Modificador pim = PedidoItem_Modificador.builder()
                .id(1L)
                .modificador(mod)
                .build();

        pedidoItem = new PedidoItem();
        pedidoItem.setId(50L);
        pedidoItem.setPedido(pedido);
        pedidoItem.setProducto(producto);
        pedidoItem.setCantidad(2);
        pedidoItem.setNota("Bien fría");
        pedidoItem.setListoParaServir(false);
        pedidoItem.setServido(false);
        pedidoItem.setModificadores(List.of(pim));
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("Tests para obtenerComandasParaKds")
    class ObtenerComandasParaKdsTests {

        @Test
        @DisplayName("Debe retornar lista de comanda KDS para una zona válida")
        void obtenerComandasParaKds_zonaValida_retornaItemsKds() {
            when(zonaTrabajoRepository.existsByNombreIgnoreCase("BARRA")).thenReturn(true);
            when(pedidoItemRepository.findPendientesByZona("BARRA")).thenReturn(List.of(pedidoItem));

            List<PedidoItemKds> resultado = pedidoItemService.obtenerComandasParaKds("barra");

            assertThat(resultado).hasSize(1);
            PedidoItemKds itemKds = resultado.get(0);
            assertThat(itemKds.id()).isEqualTo(50L);
            assertThat(itemKds.nombre()).isEqualTo("Coca Cola");
            assertThat(itemKds.mesa()).isEqualTo(3);
            assertThat(itemKds.modificadores()).hasSize(1);
            assertThat(itemKds.modificadores().get(0).nombre()).isEqualTo("Con Hielo");
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la zona no existe")
        void obtenerComandasParaKds_zonaInexistente_lanzaExcepcion() {
            when(zonaTrabajoRepository.existsByNombreIgnoreCase("INEXISTENTE")).thenReturn(false);

            assertThatThrownBy(() -> pedidoItemService.obtenerComandasParaKds("inexistente"))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Zona de Trabajo no encontrada");
        }
    }

    @Nested
    @DisplayName("Tests para marcarPedidoItemListo y marcarPedidoItemServido")
    class MarcarEstadoItemTests {

        @Test
        @DisplayName("marcarPedidoItemListo debe actualizar listoParaServir, recalcular pedido y publicar SSE")
        void marcarPedidoItemListo_itemValido_actualizaYNotifica() {
            when(pedidoItemRepository.findById(50L)).thenReturn(Optional.of(pedidoItem));

            pedidoItemService.marcarPedidoItemListo(50L);

            assertThat(pedidoItem.isListoParaServir()).isTrue();
            verify(pedidoItemRepository).save(pedidoItem);
            verify(pedidoService).recalcularEstadoPedido(10L);
            verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("marcarPedidoItemServido debe actualizar listoParaServir y servido, recalcular y notificar SSE")
        void marcarPedidoItemServido_itemValido_actualizaYNotifica() {
            when(pedidoItemRepository.findById(50L)).thenReturn(Optional.of(pedidoItem));

            pedidoItemService.marcarPedidoItemServido(50L);

            assertThat(pedidoItem.isListoParaServir()).isTrue();
            assertThat(pedidoItem.isServido()).isTrue();
            verify(pedidoItemRepository).save(pedidoItem);
            verify(pedidoService).recalcularEstadoPedido(10L);
            verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("marcarPedidoItemListo debe lanzar NoEncontradoException si el ID no existe")
        void marcarPedidoItemListo_inexistente_lanzaExcepcion() {
            when(pedidoItemRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoItemService.marcarPedidoItemListo(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("PedidoItem no encontrado para el ID: 999");
        }
    }
}
