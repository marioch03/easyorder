package com.easyorder.api.backend.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.easyorder.api.backend.dto.CrearMesaDTO;
import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.RecursoExistenteException;
import com.easyorder.api.backend.service.MesaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class MesaControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MesaService mesaService;

    @InjectMocks
    private MesaController mesaController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(mesaController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /admin/mesas")
    class ListarMesasEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la lista de mesas")
        void listarMesas_retornaListaMesasYStatus200() throws Exception {
            MesaDTO mesa1 = new MesaDTO(1L, 1, "LIBRE", "Interior", null);
            MesaDTO mesa2 = new MesaDTO(2L, 2, "OCUPADA", "Terraza", new SesionDTO(10L, "qr-123"));

            when(mesaService.listarMesas()).thenReturn(List.of(mesa1, mesa2));

            mockMvc.perform(get("/admin/mesas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].numero").value(1))
                    .andExpect(jsonPath("$[0].estado").value("LIBRE"))
                    .andExpect(jsonPath("$[1].numero").value(2))
                    .andExpect(jsonPath("$[1].sesionActiva.qrCodeUrl").value("qr-123"));
        }
    }

    @Nested
    @DisplayName("POST /admin/mesas")
    class CrearMesaEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 201 Created cuando los datos son válidos")
        void crearMesa_datosValidos_retorna201() throws Exception {
            CrearMesaDTO request = new CrearMesaDTO(5, 1L);
            MesaDTO respuesta = new MesaDTO(10L, 5, "LIBRE", "Interior", null);

            when(mesaService.crearMesa(any(CrearMesaDTO.class))).thenReturn(respuesta);

            mockMvc.perform(post("/admin/mesas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.numero").value(5))
                    .andExpect(jsonPath("$.estado").value("LIBRE"))
                    .andExpect(jsonPath("$.zona").value("Interior"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 400 Bad Request cuando el número de mesa es inválido (menor a 1)")
        void crearMesa_numeroInvalido_retorna400() throws Exception {
            CrearMesaDTO request = new CrearMesaDTO(0, 1L);

            mockMvc.perform(post("/admin/mesas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Error de validación en la petición"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 400 Bad Request cuando la zona es nula")
        void crearMesa_zonaNula_retorna400() throws Exception {
            CrearMesaDTO request = new CrearMesaDTO(5, null);

            mockMvc.perform(post("/admin/mesas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Error de validación en la petición"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 409 Conflict cuando la mesa ya existe")
        void crearMesa_mesaDuplicada_retorna409() throws Exception {
            CrearMesaDTO request = new CrearMesaDTO(5, 1L);
            when(mesaService.crearMesa(any(CrearMesaDTO.class)))
                    .thenThrow(new RecursoExistenteException("La mesa ya existe"));

            mockMvc.perform(post("/admin/mesas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("La mesa ya existe"));
        }
    }

    @Nested
    @DisplayName("PUT /admin/mesas/{id}")
    class CambiarEstadoEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la mesa actualizada")
        void cambiarEstado_mesaYEstadoValidos_retorna200() throws Exception {
            MesaDTO respuesta = new MesaDTO(1L, 1, "OCUPADA", "Interior", null);
            when(mesaService.cambiarEstado(eq(1L), eq("OCUPADA"))).thenReturn(respuesta);

            mockMvc.perform(put("/admin/mesas/1")
                            .param("estado", "OCUPADA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value("OCUPADA"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 cuando la mesa o el estado no existen")
        void cambiarEstado_mesaNoExiste_retorna404() throws Exception {
            when(mesaService.cambiarEstado(eq(999L), eq("OCUPADA")))
                    .thenThrow(new NoEncontradoException("Mesa no encontrada. Id: 999"));

            mockMvc.perform(put("/admin/mesas/999")
                            .param("estado", "OCUPADA"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Mesa no encontrada. Id: 999"));
        }
    }

    @Nested
    @DisplayName("DELETE /admin/mesas/{numero}")
    class EliminarMesaEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 cuando se elimina la mesa con éxito")
        void eliminarMesa_mesaExiste_retorna200() throws Exception {
            doNothing().when(mesaService).eliminarMesa(5);

            mockMvc.perform(delete("/admin/mesas/5"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Mesa eliminada correctamente"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 cuando la mesa no existe")
        void eliminarMesa_mesaNoExiste_retorna404() throws Exception {
            doThrow(new NoEncontradoException("Mesa no encontrada. Numero: 99"))
                    .when(mesaService).eliminarMesa(99);

            mockMvc.perform(delete("/admin/mesas/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Mesa no encontrada. Numero: 99"));
        }
    }

    @Nested
    @DisplayName("PUT /cliente/mesas/cuenta")
    class SolicitarCuentaEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 cuando el cliente solicita la cuenta con header válido")
        void clienteSolicitaCuenta_headerValido_retorna200() throws Exception {
            MesaDTO respuesta = new MesaDTO(1L, 3, "ESPERANDO_CUENTA", "Terraza", new SesionDTO(5L, "valid-session-code"));
            when(mesaService.solicitarCuenta("valid-session-code")).thenReturn(respuesta);

            mockMvc.perform(put("/cliente/mesas/cuenta")
                            .header("X-Session-Code", "valid-session-code"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado").value("ESPERANDO_CUENTA"))
                    .andExpect(jsonPath("$.sesionActiva.qrCodeUrl").value("valid-session-code"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 si la sesión no existe o no está activa")
        void clienteSolicitaCuenta_sesionInvalida_retorna404() throws Exception {
            when(mesaService.solicitarCuenta("invalid-code"))
                    .thenThrow(new NoEncontradoException("La sesión no está activa"));

            mockMvc.perform(put("/cliente/mesas/cuenta")
                            .header("X-Session-Code", "invalid-code"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("La sesión no está activa"));
        }
    }
}
