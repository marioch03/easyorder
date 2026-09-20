package com.easyorder.api.backend.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.dto.CrearPedidoDTO;
import com.easyorder.api.backend.dto.CrearPedidoItemDTO;
import com.easyorder.api.backend.dto.CuentaDTO;
import com.easyorder.api.backend.dto.PedidoDTO;
import com.easyorder.api.backend.dto.PedidoItemDTO;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private PedidoDTO pedidoDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        pedidoDTO = new PedidoDTO(
                1L,
                10L,
                5,
                "PENDIENTE",
                Instant.now(),
                List.of()
        );
    }

    @Nested
    @DisplayName("GET /admin/pedidos y /admin/pedidos/{id}/items")
    class AdminPedidosListTests {

        @Test
        @DisplayName("GET /admin/pedidos - Retorna HTTP 200 y lista de pedidos")
        void listarPedidos_retorna200() throws Exception {
            when(pedidoService.listarPedidos()).thenReturn(List.of(pedidoDTO));

            mockMvc.perform(get("/admin/pedidos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].idPedido").value(1L))
                    .andExpect(jsonPath("$[0].nombreEstado").value("PENDIENTE"));
        }

        @Test
        @DisplayName("GET /admin/pedidos/{id}/items - Retorna HTTP 200 y items del pedido")
        void getPedidoItems_retorna200() throws Exception {
            PedidoItemDTO itemDTO = new PedidoItemDTO(1L, 100L, "Burger", 2, new BigDecimal("10.00"), "Sin cebolla", false, false, List.of());
            when(pedidoService.getPedidoItems(1L)).thenReturn(List.of(itemDTO));

            mockMvc.perform(get("/admin/pedidos/1/items"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombreProducto").value("Burger"));
        }
    }

    @Nested
    @DisplayName("GET /admin/pedidos/{idMesa}/cuenta y /cliente/pedidos/cuenta")
    class CuentaEndpointsTests {

        @Test
        @DisplayName("GET /admin/pedidos/{idMesa}/cuenta - Retorna HTTP 200 y CuentaDTO")
        void getCuentaAdmin_retorna200() throws Exception {
            CuentaDTO cuenta = new CuentaDTO(List.of(), new BigDecimal("25.00"));
            when(pedidoService.obtenerCuentaAdmin(1L)).thenReturn(cuenta);

            mockMvc.perform(get("/admin/pedidos/1/cuenta"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(25.00));
        }

        @Test
        @DisplayName("GET /cliente/pedidos/cuenta - Retorna HTTP 200 y CuentaDTO")
        void getCuentaCliente_retorna200() throws Exception {
            CuentaDTO cuenta = new CuentaDTO(List.of(), new BigDecimal("25.00"));
            when(pedidoService.obtenerCuentaCliente("session-qr-123")).thenReturn(cuenta);

            mockMvc.perform(get("/cliente/pedidos/cuenta")
                            .header("X-Session-Code", "session-qr-123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(25.00));
        }
    }

    @Nested
    @DisplayName("POST /admin/pedidos/mesa/{idMesa} y POST /cliente/pedidos")
    class CrearPedidoEndpointsTests {

        @Test
        @DisplayName("POST /admin/pedidos/mesa/{idMesa} - Crea pedido y retorna HTTP 201")
        void crearPedidoAdmin_retorna201() throws Exception {
            CrearPedidoItemDTO item = new CrearPedidoItemDTO(
                    null, 100L, "Burger", 1, new BigDecimal("10.00"), null, false, false, List.of());
            CrearPedidoDTO request = new CrearPedidoDTO(List.of(item), new BigDecimal("10.00"));

            when(pedidoService.crearPedidoAdmin(any(CrearPedidoDTO.class), eq(1L))).thenReturn(pedidoDTO);

            mockMvc.perform(post("/admin/pedidos/mesa/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPedido").value(1L));
        }

        @Test
        @DisplayName("POST /cliente/pedidos - Crea pedido y retorna HTTP 201")
        void crearPedidoCliente_retorna201() throws Exception {
            CrearPedidoItemDTO item = new CrearPedidoItemDTO(
                    null, 100L, "Burger", 1, new BigDecimal("10.00"), null, false, false, List.of());
            CrearPedidoDTO request = new CrearPedidoDTO(List.of(item), new BigDecimal("10.00"));

            when(pedidoService.crearPedidoCliente(any(CrearPedidoDTO.class), eq("session-qr-123"))).thenReturn(pedidoDTO);

            mockMvc.perform(post("/cliente/pedidos")
                            .header("X-Session-Code", "session-qr-123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPedido").value(1L));
        }
    }

    @Nested
    @DisplayName("PATCH /admin/pedidos/{id}/servido")
    class MarcarPedidoServidoEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 204 No Content cuando se marca como servido")
        void marcarPedidoServido_retorna204() throws Exception {
            doNothing().when(pedidoService).marcarPedidoServido(1L);

            mockMvc.perform(patch("/admin/pedidos/1/servido"))
                    .andExpect(status().isNoContent());

            verify(pedidoService).marcarPedidoServido(1L);
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 si el pedido no existe")
        void marcarPedidoServido_noExiste_retorna404() throws Exception {
            doThrow(new NoEncontradoException("Pedido no encontrado. Id: 999"))
                    .when(pedidoService).marcarPedidoServido(999L);

            mockMvc.perform(patch("/admin/pedidos/999/servido"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Pedido no encontrado. Id: 999"));
        }
    }

    @Nested
    @DisplayName("GET /cliente/pedidos")
    class GetPedidosPorSesionEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y los pedidos de la sesión activa")
        void getPedidosPorSesion_retorna200() throws Exception {
            when(pedidoService.listarPedidosPorSesion("session-qr-123")).thenReturn(List.of(pedidoDTO));

            mockMvc.perform(get("/cliente/pedidos")
                            .header("X-Session-Code", "session-qr-123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].idPedido").value(1L));
        }
    }
}
