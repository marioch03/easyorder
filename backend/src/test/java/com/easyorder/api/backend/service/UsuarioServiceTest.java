package com.easyorder.api.backend.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.easyorder.api.backend.dto.UsuarioDTO;
import com.easyorder.api.backend.dto.UsuarioRolDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.model.UsuarioRol;
import com.easyorder.api.backend.repository.UsuarioRepository;
import com.easyorder.api.backend.repository.UsuarioRolRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRol rolAdmin;

    @BeforeEach
    void setUp() {
        rolAdmin = new UsuarioRol("ADMIN", "Administrador del sistema");
        rolAdmin.setId(1L);

        usuario = Usuario.builder()
                .id(10L)
                .nombre("juan")
                .clave("encoded-password")
                .rol(rolAdmin)
                .activo(true)
                .tenantId(1L)
                .build();
    }

    @Nested
    @DisplayName("Tests para deshabilitarUsuario")
    class DeshabilitarUsuarioTests {

        @Test
        @DisplayName("Debe deshabilitar el usuario correctamente cuando existe")
        void deshabilitarUsuario_usuarioExiste_deshabilitaYGuarda() {
            when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

            usuarioService.deshabilitarUsuario(10L);

            assertThat(usuario.isActivo()).isFalse();
            verify(usuarioRepository).save(usuario);
        }

        @Test
        @DisplayName("Debe lanzar EntityNotFoundException si el usuario no existe")
        void deshabilitarUsuario_usuarioNoExiste_lanzaExcepcion() {
            when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.deshabilitarUsuario(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Usuario no encontrado. ID: 999");
        }
    }

    @Nested
    @DisplayName("Tests para getAllRoles")
    class GetAllRolesTests {

        @Test
        @DisplayName("Debe retornar la lista mapeada de UsuarioRolDTO")
        void getAllRoles_retornaListaDTO() {
            when(usuarioRolRepository.findAll()).thenReturn(List.of(rolAdmin));

            List<UsuarioRolDTO> roles = usuarioService.getAllRoles();

            assertThat(roles).hasSize(1);
            assertThat(roles.get(0).id()).isEqualTo(1L);
            assertThat(roles.get(0).nombre()).isEqualTo("ADMIN");
        }
    }

    @Nested
    @DisplayName("Tests para getAllUsers")
    class GetAllUsersTests {

        @Test
        @DisplayName("Debe retornar la lista mapeada de UsuarioDTO")
        void getAllUsers_retornaListaDTO() {
            when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

            List<UsuarioDTO> usuarios = usuarioService.getAllUsers();

            assertThat(usuarios).hasSize(1);
            assertThat(usuarios.get(0).id()).isEqualTo(10L);
            assertThat(usuarios.get(0).nombre()).isEqualTo("juan");
            assertThat(usuarios.get(0).activo()).isTrue();
        }
    }
}
