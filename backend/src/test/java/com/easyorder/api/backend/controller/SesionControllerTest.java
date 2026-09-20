package com.easyorder.api.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.dto.SesionClienteDTO;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.RecursoExistenteException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.MesaEstado;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;
import com.easyorder.api.backend.service.SesionService;

@ExtendWith(MockitoExtension.class)
class SesionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SesionService sesionService;

    @InjectMocks
    private SesionController sesionController;

    private Mesa mesa;
    private Sesion sesion;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sesionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        MesaEstado mesaEstado = new MesaEstado("OCUPADA");
        mesaEstado.setId(1L);

        mesa = new Mesa();
        mesa.setId(10L);
        mesa.setNumero(3);
        mesa.setEstado(mesaEstado);

        SesionEstado sesionEstado = new SesionEstado("ACTIVA", "Sesión activa");
        sesionEstado.setId(1L);

        sesion = new Sesion(mesa, null, sesionEstado, "qr-12345");
        sesion.setId(100L);
    }

    @Nested
    @DisplayName("POST /admin/sesiones/open/{idMesa}")
    class CrearSesionEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 201 Created cuando se abre una sesión con éxito")
        void crearSesion_mesaLibre_retorna201() throws Exception {
            when(sesionService.crearSesion(10L)).thenReturn(sesion);

            mockMvc.perform(post("/admin/sesiones/open/10"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(100L))
                    .andExpect(jsonPath("$.qrCodeUrl").value("qr-12345"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 409 Conflict si la mesa ya tiene una sesión activa")
        void crearSesion_mesaConSesionActiva_retorna409() throws Exception {
            when(sesionService.crearSesion(10L))
                    .thenThrow(new RecursoExistenteException("La mesa ya tiene una sesión activa"));

            mockMvc.perform(post("/admin/sesiones/open/10"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("La mesa ya tiene una sesión activa"));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 Not Found si la mesa no existe")
        void crearSesion_mesaNoExiste_retorna404() throws Exception {
            when(sesionService.crearSesion(999L))
                    .thenThrow(new NoEncontradoException("Mesa no encontrada. Id: 999"));

            mockMvc.perform(post("/admin/sesiones/open/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Mesa no encontrada. Id: 999"));
        }
    }

    @Nested
    @DisplayName("POST /admin/sesiones/close/{sessionCode}")
    class CerrarSesionEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 OK cuando se cierra la sesión")
        void cerrarSesion_codigoValido_retorna200() throws Exception {
            SesionEstado estadoFinalizada = new SesionEstado("FINALIZADA", "Cerrada");
            sesion.setEstado(estadoFinalizada);

            when(sesionService.cerrarSesion("qr-12345")).thenReturn(sesion);

            mockMvc.perform(post("/admin/sesiones/close/qr-12345"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100L));
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 Not Found si el código de sesión no existe")
        void cerrarSesion_codigoInvalido_retorna404() throws Exception {
            when(sesionService.cerrarSesion("qr-invalido"))
                    .thenThrow(new NoEncontradoException("Sesión no encontrada para el QR code: qr-invalido"));

            mockMvc.perform(post("/admin/sesiones/close/qr-invalido"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Sesión no encontrada para el QR code: qr-invalido"));
        }
    }

    @Nested
    @DisplayName("GET /cliente/sesiones/mesa")
    class GetMesaPorCodigoEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la mesa correspondiente al código de sesión")
        void getMesaPorCodigo_headerValido_retornaMesa() throws Exception {
            when(sesionService.getMesaPorCodigo("qr-12345")).thenReturn(mesa);

            mockMvc.perform(get("/cliente/sesiones/mesa")
                            .header("X-Session-Code", "qr-12345"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10L))
                    .andExpect(jsonPath("$.numero").value(3));
        }
    }

    @Nested
    @DisplayName("GET /cliente/sesiones/init")
    class GetDatosClienteEndpointTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y SesionClienteDTO")
        void getDatosCliente_headerValido_retornaSesionClienteDTO() throws Exception {
            SesionClienteDTO dto = new SesionClienteDTO(100L, "qr-12345", 10L, 3, "OCUPADA");
            when(sesionService.obtenerDatosSesionCliente(eq("qr-12345"))).thenReturn(dto);

            mockMvc.perform(get("/cliente/sesiones/init")
                            .header("X-Session-Code", "qr-12345"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sesionId").value(100L))
                    .andExpect(jsonPath("$.sessionCode").value("qr-12345"))
                    .andExpect(jsonPath("$.mesaId").value(10L))
                    .andExpect(jsonPath("$.numeroMesa").value(3))
                    .andExpect(jsonPath("$.estadoMesa").value("OCUPADA"));
        }
    }
}
