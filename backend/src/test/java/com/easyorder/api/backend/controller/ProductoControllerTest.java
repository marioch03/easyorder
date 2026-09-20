package com.easyorder.api.backend.controller;

import java.math.BigDecimal;
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.dto.EditarProductoDTO;
import com.easyorder.api.backend.dto.ProductoComandaDTO;
import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ProductoDTO productoDTO;
    private ProductoTipoDTO tipoDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        productoDTO = new ProductoDTO(
                1L,
                "Burger Clásica",
                "Con queso",
                12.50,
                true,
                "burger.jpg",
                10L,
                List.of(),
                List.of()
        );

        tipoDTO = new ProductoTipoDTO(10L, "Hamburguesas");
    }

    @Nested
    @DisplayName("GET /cliente/productos y GET /admin/productos")
    class GetProductosEndpointTests {

        @Test
        @DisplayName("GET /cliente/productos - Debe retornar HTTP 200 y la lista de productos")
        void getProductosCliente_retornaListaYStatus200() throws Exception {
            when(productoService.findAll()).thenReturn(List.of(productoDTO));

            mockMvc.perform(get("/cliente/productos")
                            .header("X-Session-Code", "qr-123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Burger Clásica"))
                    .andExpect(jsonPath("$[0].precio").value(12.50));
        }

        @Test
        @DisplayName("GET /admin/productos - Debe retornar HTTP 200 y la lista de productos")
        void getProductosAdmin_retornaListaYStatus200() throws Exception {
            when(productoService.findAll()).thenReturn(List.of(productoDTO));

            mockMvc.perform(get("/admin/productos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Burger Clásica"));
        }
    }

    @Nested
    @DisplayName("GET /admin/productos/{id}")
    class GetProductoPorIdEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 cuando el producto existe")
        void getProducto_cuandoExiste_retorna200() throws Exception {
            ProductoTipo tipo = new ProductoTipo("Hamburguesas", "Desc");
            tipo.setId(10L);

            Producto p = new Producto();
            p.setId(1L);
            p.setNombre("Burger Clásica");
            p.setPrecio(new BigDecimal("12.50"));
            p.setTipo(tipo);

            when(productoService.getProducto(1L)).thenReturn(p);

            mockMvc.perform(get("/admin/productos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Burger Clásica"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 cuando el producto no existe")
        void getProducto_cuandoNoExiste_retorna404() throws Exception {
            when(productoService.getProducto(999L))
                    .thenThrow(new NoEncontradoException("Producto no encontrado. ID: 999"));

            mockMvc.perform(get("/admin/productos/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Producto no encontrado. ID: 999"));
        }
    }

    @Nested
    @DisplayName("PUT /admin/productos/{id}")
    class EditarProductoEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y el DTO editado")
        void editarProducto_datosValidos_retorna200() throws Exception {
            EditarProductoDTO request = new EditarProductoDTO(1L, "Burger Premium", "Con trufa", 16.0, 10L, true);
            when(productoService.editarProducto(eq(1L), any(EditarProductoDTO.class))).thenReturn(request);

            mockMvc.perform(put("/admin/productos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Burger Premium"))
                    .andExpect(jsonPath("$.precio").value(16.0));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 si el producto no existe")
        void editarProducto_noExiste_retorna404() throws Exception {
            EditarProductoDTO request = new EditarProductoDTO(999L, "Burger", "Desc", 10.0, 10L, true);
            when(productoService.editarProducto(eq(999L), any(EditarProductoDTO.class)))
                    .thenThrow(new NoEncontradoException("Producto no encontrado. ID: 999"));

            mockMvc.perform(put("/admin/productos/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Producto no encontrado. ID: 999"));
        }
    }

    @Nested
    @DisplayName("DELETE /admin/productos/{id}")
    class EliminarProductoEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 204 No Content cuando se elimina el producto")
        void eliminarProducto_cuandoExiste_retorna204() throws Exception {
            doNothing().when(productoService).deleteById(1L);

            mockMvc.perform(delete("/admin/productos/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 cuando el producto a eliminar no existe")
        void eliminarProducto_cuandoNoExiste_retorna404() throws Exception {
            doThrow(new NoEncontradoException("Producto no encontrado. ID: 999"))
                    .when(productoService).deleteById(999L);

            mockMvc.perform(delete("/admin/productos/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Producto no encontrado. ID: 999"));
        }
    }

    @Nested
    @DisplayName("GET /admin/productos/simplified")
    class GetProductosSimplificadosEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la lista simplificada para comandas")
        void getProductosSimplificados_retorna200() throws Exception {
            ProductoComandaDTO comandaDTO = new ProductoComandaDTO(1L, "Burger Clásica", new BigDecimal("12.50"), 10L, true, List.of());
            when(productoService.getProductosSimplificados()).thenReturn(List.of(comandaDTO));

            mockMvc.perform(get("/admin/productos/simplified"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Burger Clásica"));
        }
    }

    @Nested
    @DisplayName("GET /admin/productos/tipos y /admin/productos/tipos/{nombreZonaTrabajo}")
    class GetTiposEndpointTests {

        @Test
        @DisplayName("GET /admin/productos/tipos - Debe retornar HTTP 200 y todos los tipos")
        void getTipos_retorna200() throws Exception {
            when(productoService.getTipos()).thenReturn(List.of(tipoDTO));

            mockMvc.perform(get("/admin/productos/tipos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Hamburguesas"));
        }

        @Test
        @DisplayName("GET /admin/productos/tipos/{nombreZonaTrabajo} - Debe retornar tipos para KDS")
        void getTiposKds_retorna200() throws Exception {
            when(productoService.getTiposKds("COCINA")).thenReturn(List.of(tipoDTO));

            mockMvc.perform(get("/admin/productos/tipos/COCINA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("Hamburguesas"));
        }
    }
}
