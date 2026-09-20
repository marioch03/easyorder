package com.easyorder.api.backend.controller;

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

import com.easyorder.api.backend.dto.UsuarioDTO;
import com.easyorder.api.backend.dto.UsuarioRolDTO;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /admin/usuarios")
    class GetUsuariosTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la lista de usuarios")
        void getUsuarios_retorna200() throws Exception {
            UsuarioDTO dto = new UsuarioDTO(1L, "admin", true);
            when(usuarioService.getAllUsers()).thenReturn(List.of(dto));

            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("admin"))
                    .andExpect(jsonPath("$[0].activo").value(true));
        }
    }

    @Nested
    @DisplayName("PATCH /admin/usuarios/{id}")
    class DeshabilitarUsuarioTests {

        @Test
        @DisplayName("Debe retornar HTTP 204 No Content cuando el usuario es deshabilitado")
        void deshabilitarUsuario_cuandoExiste_retorna204() throws Exception {
            doNothing().when(usuarioService).deshabilitarUsuario(1L);

            mockMvc.perform(patch("/admin/usuarios/1"))
                    .andExpect(status().isNoContent());

            verify(usuarioService).deshabilitarUsuario(1L);
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 si el usuario no existe")
        void deshabilitarUsuario_cuandoNoExiste_retorna404() throws Exception {
            doThrow(new NoEncontradoException("Usuario no encontrado. ID: 999"))
                    .when(usuarioService).deshabilitarUsuario(999L);

            mockMvc.perform(patch("/admin/usuarios/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Usuario no encontrado. ID: 999"));
        }
    }

    @Nested
    @DisplayName("GET /admin/usuarios/roles")
    class GetRolesTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 y la lista de roles")
        void getRoles_retorna200() throws Exception {
            UsuarioRolDTO rolDTO = new UsuarioRolDTO(1L, "ADMIN");
            when(usuarioService.getAllRoles()).thenReturn(List.of(rolDTO));

            mockMvc.perform(get("/admin/usuarios/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nombre").value("ADMIN"));
        }
    }
}
