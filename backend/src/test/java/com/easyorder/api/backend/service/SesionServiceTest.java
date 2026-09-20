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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.easyorder.api.backend.dto.SesionAuthProjection;
import com.easyorder.api.backend.dto.SesionClienteDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.RecursoExistenteException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.MesaEstado;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;
import com.easyorder.api.backend.repository.MesaEstadoRepository;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.SesionEstadoRepository;
import com.easyorder.api.backend.repository.SesionRepository;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class SesionServiceTest {

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private SesionEstadoRepository sesionEstadoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private MesaEstadoRepository mesaEstadoRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SesionService sesionService;

    private Mesa mesa;
    private MesaEstado mesaEstadoLibre;
    private MesaEstado mesaEstadoOcupada;
    private SesionEstado sesionEstadoActiva;
    private SesionEstado sesionEstadoFinalizada;
    private Sesion sesion;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);

        mesaEstadoLibre = new MesaEstado("LIBRE");
        mesaEstadoLibre.setId(1L);

        mesaEstadoOcupada = new MesaEstado("OCUPADA");
        mesaEstadoOcupada.setId(2L);

        mesa = new Mesa();
        mesa.setId(10L);
        mesa.setNumero(1);
        mesa.setTenantId(1L);
        mesa.setEstado(mesaEstadoLibre);

        sesionEstadoActiva = new SesionEstado("ACTIVA", "Sesión abierta");
        sesionEstadoActiva.setId(1L);

        sesionEstadoFinalizada = new SesionEstado("FINALIZADA", "Sesión cerrada");
        sesionEstadoFinalizada.setId(2L);

        sesion = new Sesion(mesa, null, sesionEstadoActiva, "uuid-qr-code-test");
        sesion.setId(100L);
        sesion.setTenantId(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("Tests para listarSesiones")
    class ListarSesionesTests {

        @Test
        @DisplayName("Debe retornar la lista de todas las sesiones")
        void listarSesiones_retornaListaSesiones() {
            when(sesionRepository.findAll()).thenReturn(List.of(sesion));

            List<Sesion> resultado = sesionService.listarSesiones();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getQrCodeUrl()).isEqualTo("uuid-qr-code-test");
        }
    }

    @Nested
    @DisplayName("Tests para getSesion")
    class GetSesionTests {

        @Test
        @DisplayName("Debe retornar la sesión cuando existe el ID")
        void getSesion_cuandoExiste_retornaSesion() {
            when(sesionRepository.findById(100L)).thenReturn(Optional.of(sesion));

            Sesion resultado = sesionService.getSesion(100L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando no existe el ID")
        void getSesion_cuandoNoExiste_lanzaNoEncontradoException() {
            when(sesionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sesionService.getSesion(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Sesión no encontrada. Id: 999");
        }
    }

    @Nested
    @DisplayName("Tests para validarSesion")
    class ValidarSesionTests {

        @Test
        @DisplayName("Debe retornar true si el código de sesión activo existe")
        void validarSesion_cuandoEsActiva_retornaTrue() {
            when(sesionRepository.existsByQrCodeUrlAndEstadoNombreUnfiltered("code-123", "ACTIVA"))
                    .thenReturn(1);

            boolean valida = sesionService.validarSesion("code-123");

            assertThat(valida).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false si el código de sesión no existe o no está activa")
        void validarSesion_cuandoNoExiste_retornaFalse() {
            when(sesionRepository.existsByQrCodeUrlAndEstadoNombreUnfiltered("code-invalido", "ACTIVA"))
                    .thenReturn(0);

            boolean valida = sesionService.validarSesion("code-invalido");

            assertThat(valida).isFalse();
        }
    }

    @Nested
    @DisplayName("Tests para eliminarSesion")
    class EliminarSesionTests {

        @Test
        @DisplayName("Debe eliminar la sesión cuando existe")
        void eliminarSesion_cuandoExiste_eliminaPorId() {
            when(sesionRepository.existsById(100L)).thenReturn(true);

            sesionService.eliminarSesion(100L);

            verify(sesionRepository).deleteById(100L);
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando no existe el ID")
        void eliminarSesion_cuandoNoExiste_lanzaNoEncontradoException() {
            when(sesionRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> sesionService.eliminarSesion(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Sesión no encontrada. Id: 999");

            verify(sesionRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Tests para crearSesion")
    class CrearSesionTests {

        @Test
        @DisplayName("Debe crear una nueva sesión, cambiar mesa a OCUPADA y publicar evento SSE")
        void crearSesion_mesaLibre_creaYPublicaSse() {
            when(mesaRepository.findById(10L)).thenReturn(Optional.of(mesa));
            when(sesionEstadoRepository.findByNombre("ACTIVA")).thenReturn(Optional.of(sesionEstadoActiva));
            when(sesionRepository.findByMesaAndEstado(mesa, sesionEstadoActiva)).thenReturn(Optional.empty());
            when(mesaEstadoRepository.findByNombre("OCUPADA")).thenReturn(Optional.of(mesaEstadoOcupada));
            when(mesaRepository.save(mesa)).thenReturn(mesa);
            when(sesionRepository.save(any(Sesion.class))).thenAnswer(i -> {
                Sesion s = i.getArgument(0);
                s.setId(101L);
                return s;
            });

            Sesion resultado = sesionService.crearSesion(10L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(101L);
            assertThat(resultado.getEstado().getNombre()).isEqualTo("ACTIVA");
            assertThat(resultado.getQrCodeUrl()).isNotBlank();
            assertThat(mesa.getEstado()).isEqualTo(mesaEstadoOcupada);

            verify(mesaRepository).save(mesa);
            verify(sesionRepository).save(any(Sesion.class));
            verify(eventPublisher).publishEvent(any(com.easyorder.api.backend.event.SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar RecursoExistenteException si la mesa ya tiene una sesión activa")
        void crearSesion_mesaConSesionActiva_lanzaRecursoExistenteException() {
            when(mesaRepository.findById(10L)).thenReturn(Optional.of(mesa));
            when(sesionEstadoRepository.findByNombre("ACTIVA")).thenReturn(Optional.of(sesionEstadoActiva));
            when(sesionRepository.findByMesaAndEstado(mesa, sesionEstadoActiva)).thenReturn(Optional.of(sesion));

            assertThatThrownBy(() -> sesionService.crearSesion(10L))
                    .isInstanceOf(RecursoExistenteException.class)
                    .hasMessageContaining("La mesa ya tiene una sesión activa");

            verify(mesaRepository, never()).save(any());
            verify(sesionRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la mesa no existe")
        void crearSesion_mesaNoExiste_lanzaNoEncontradoException() {
            when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sesionService.crearSesion(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Mesa no encontrada. Id: 999");
        }
    }

    @Nested
    @DisplayName("Tests para cerrarSesion")
    class CerrarSesionTests {

        @Test
        @DisplayName("Debe cerrar la sesión, marcar horaFin, poner mesa en LIBRE y publicar evento SSE")
        void cerrarSesion_sesionValida_cierraYPublicaSse() {
            when(sesionRepository.findByQrCodeUrlUnfiltered("uuid-qr-code-test")).thenReturn(Optional.of(sesion));
            when(mesaEstadoRepository.findByNombre("LIBRE")).thenReturn(Optional.of(mesaEstadoLibre));
            when(mesaRepository.save(mesa)).thenReturn(mesa);
            when(sesionEstadoRepository.findByNombre("FINALIZADA")).thenReturn(Optional.of(sesionEstadoFinalizada));
            when(sesionRepository.save(sesion)).thenReturn(sesion);

            Sesion resultado = sesionService.cerrarSesion("uuid-qr-code-test");

            assertThat(resultado).isNotNull();
            assertThat(resultado.getEstado().getNombre()).isEqualTo("FINALIZADA");
            assertThat(resultado.getHoraFin()).isNotNull();
            assertThat(mesa.getEstado()).isEqualTo(mesaEstadoLibre);

            verify(mesaRepository).save(mesa);
            verify(sesionRepository).save(sesion);
            verify(eventPublisher).publishEvent(any(com.easyorder.api.backend.event.SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando el código de sesión no existe")
        void cerrarSesion_codigoInvalido_lanzaNoEncontradoException() {
            when(sesionRepository.findByQrCodeUrlUnfiltered("codigo-invalido")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sesionService.cerrarSesion("codigo-invalido"))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Sesión no encontrada para el QR code");
        }
    }

    @Nested
    @DisplayName("Tests para obtenerDatosSesionCliente")
    class ObtenerDatosSesionClienteTests {

        @Test
        @DisplayName("Debe retornar SesionClienteDTO con los datos de la sesión y mesa")
        void obtenerDatosSesionCliente_sesionValida_retornaDTO() {
            when(sesionRepository.findByQrCodeUrlUnfiltered("uuid-qr-code-test")).thenReturn(Optional.of(sesion));

            SesionClienteDTO dto = sesionService.obtenerDatosSesionCliente("uuid-qr-code-test");

            assertThat(dto).isNotNull();
            assertThat(dto.sesionId()).isEqualTo(100L);
            assertThat(dto.sessionCode()).isEqualTo("uuid-qr-code-test");
            assertThat(dto.mesaId()).isEqualTo(10L);
            assertThat(dto.numeroMesa()).isEqualTo(1);
            assertThat(dto.estadoMesa()).isEqualTo("LIBRE");
        }
    }

    @Nested
    @DisplayName("Tests para getSesionActivaParaAutenticacion")
    class GetSesionActivaParaAutenticacionTests {

        @Test
        @DisplayName("Debe retornar la proyección de autenticación cuando la sesión está ACTIVA")
        void getSesionActivaParaAutenticacion_activa_retornaProyeccion() {
            SesionAuthProjection proyeccion = new SesionAuthProjection() {
                @Override
                public Long getId() {
                    return 100L;
                }

                @Override
                public Long getTenantId() {
                    return 1L;
                }

                @Override
                public String getEstadoNombre() {
                    return "ACTIVA";
                }
            };

            when(sesionRepository.findAuthProjectionByQrCodeUrl("code-123")).thenReturn(Optional.of(proyeccion));

            SesionAuthProjection resultado = sesionService.getSesionActivaParaAutenticacion("code-123");

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(100L);
            assertThat(resultado.getTenantId()).isEqualTo(1L);
            assertThat(resultado.getEstadoNombre()).isEqualTo("ACTIVA");
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la sesión existe pero no está ACTIVA")
        void getSesionActivaParaAutenticacion_inactiva_lanzaNoEncontradoException() {
            SesionAuthProjection proyeccion = new SesionAuthProjection() {
                @Override
                public Long getId() {
                    return 100L;
                }

                @Override
                public Long getTenantId() {
                    return 1L;
                }

                @Override
                public String getEstadoNombre() {
                    return "FINALIZADA";
                }
            };

            when(sesionRepository.findAuthProjectionByQrCodeUrl("code-123")).thenReturn(Optional.of(proyeccion));

            assertThatThrownBy(() -> sesionService.getSesionActivaParaAutenticacion("code-123"))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("La sesión no está activa");
        }
    }
}
