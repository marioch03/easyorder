package com.easyorder.api.backend.controller;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.dto.PedidoItemKds;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.service.PedidoItemService;

@ExtendWith(MockitoExtension.class)
class PedidoItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoItemService pedidoItemService;

    @InjectMocks
    private PedidoItemController pedidoItemController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoItemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /admin/comandas/kds/{nombreZonaTrabajo}")
    class GetPedidoItemKdsTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la lista de comandas KDS")
        void getPedidoItemKds_retorna200() throws Exception {
            PedidoItemKds kds = new PedidoItemKds(
                    1L,
                    "Hamburguesa",
                    10L,
                    2,
                    "Sin cebolla",
                    4,
                    false,
                    Instant.now(),
                    List.of()
            );

            when(pedidoItemService.obtenerComandasParaKds("COCINA")).thenReturn(List.of(kds));

            mockMvc.perform(get("/admin/comandas/kds/COCINA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Hamburguesa"))
                    .andExpect(jsonPath("$[0].mesa").value(4));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 si la zona no existe")
        void getPedidoItemKds_zonaInexistente_retorna404() throws Exception {
            when(pedidoItemService.obtenerComandasParaKds("ZONA_FANTASMA"))
                    .thenThrow(new NoEncontradoException("Zona de Trabajo no encontrada: ZONA_FANTASMA"));

            mockMvc.perform(get("/admin/comandas/kds/ZONA_FANTASMA"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Zona de Trabajo no encontrada: ZONA_FANTASMA"));
        }
    }

    @Nested
    @DisplayName("PATCH /admin/comandas/listo/{idPedidoItem} y /admin/comandas/servido/{idPedidoItem}")
    class PatchComandaItemTests {

        @Test
        @DisplayName("PATCH /admin/comandas/listo/{id} - Debe retornar HTTP 204 No Content")
        void marcarListoItem_retorna204() throws Exception {
            doNothing().when(pedidoItemService).marcarPedidoItemListo(1L);

            mockMvc.perform(patch("/admin/comandas/listo/1"))
                    .andExpect(status().isNoContent());

            verify(pedidoItemService).marcarPedidoItemListo(1L);
        }

        @Test
        @DisplayName("PATCH /admin/comandas/servido/{id} - Debe retornar HTTP 204 No Content")
        void marcaServidoItem_retorna204() throws Exception {
            doNothing().when(pedidoItemService).marcarPedidoItemServido(1L);

            mockMvc.perform(patch("/admin/comandas/servido/1"))
                    .andExpect(status().isNoContent());

            verify(pedidoItemService).marcarPedidoItemServido(1L);
        }
    }
}
