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

import com.easyorder.api.backend.dto.CrearMesaDTO;
import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.event.SseTopicEvent;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.exception.RecursoExistenteException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.MesaEstado;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;
import com.easyorder.api.backend.model.Zona;
import com.easyorder.api.backend.repository.MesaEstadoRepository;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.SesionRepository;
import com.easyorder.api.backend.repository.ZonaRepository;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private ZonaRepository zonaRepository;

    @Mock
    private MesaEstadoRepository mesaEstadoRepository;

    @Mock
    private SesionRepository sesionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MesaService mesaService;

    private Mesa mesa;
    private MesaEstado estadoLibre;
    private MesaEstado estadoOcupada;
    private MesaEstado estadoEsperandoCuenta;
    private Zona zonaPrincipal;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);

        estadoLibre = new MesaEstado("LIBRE");
        estadoLibre.setId(1L);

        estadoOcupada = new MesaEstado("OCUPADA");
        estadoOcupada.setId(2L);

        estadoEsperandoCuenta = new MesaEstado("ESPERANDO_CUENTA");
        estadoEsperandoCuenta.setId(3L);

        zonaPrincipal = new Zona();
        zonaPrincipal.setId(10L);
        zonaPrincipal.setNombre("Terraza");

        mesa = new Mesa();
        mesa.setId(100L);
        mesa.setTenantId(1L);
        mesa.setNumero(5);
        mesa.setEstado(estadoLibre);
        mesa.setZona(zonaPrincipal);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("Tests para listarMesas")
    class ListarMesasTests {

        @Test
        @DisplayName("Debe listar mesas mapeando correctamente sus zonas y sesiones activas")
        void listarMesas_conMesasYSesionActiva_retornaListaMesaDTO() {
            SesionEstado sesionActivaEstado = new SesionEstado();
            sesionActivaEstado.setNombre("ACTIVA");

            Sesion sesion = new Sesion();
            sesion.setId(200L);
            sesion.setQrCodeUrl("qr-session-code-123");
            sesion.setMesa(mesa);
            sesion.setEstado(sesionActivaEstado);

            when(mesaRepository.findAll()).thenReturn(List.of(mesa));
            when(sesionRepository.findByMesaInAndEstadoNombre(List.of(mesa), "ACTIVA"))
                    .thenReturn(List.of(sesion));

            List<MesaDTO> resultado = mesaService.listarMesas();

            assertThat(resultado).hasSize(1);
            MesaDTO dto = resultado.get(0);
            assertThat(dto.id()).isEqualTo(100L);
            assertThat(dto.numero()).isEqualTo(5);
            assertThat(dto.estado()).isEqualTo("LIBRE");
            assertThat(dto.zona()).isEqualTo("Terraza");
            assertThat(dto.sesionActiva()).isNotNull();
            assertThat(dto.sesionActiva().id()).isEqualTo(200L);
            assertThat(dto.sesionActiva().qrCodeUrl()).isEqualTo("qr-session-code-123");
        }

        @Test
        @DisplayName("Debe listar mesas con sesionActiva null si no tienen sesión activa")
        void listarMesas_sinSesionActiva_retornaSesionActivaNull() {
            when(mesaRepository.findAll()).thenReturn(List.of(mesa));
            when(sesionRepository.findByMesaInAndEstadoNombre(List.of(mesa), "ACTIVA"))
                    .thenReturn(List.of());

            List<MesaDTO> resultado = mesaService.listarMesas();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).sesionActiva()).isNull();
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay mesas registradas")
        void listarMesas_sinMesas_retornaListaVacia() {
            when(mesaRepository.findAll()).thenReturn(List.of());
            when(sesionRepository.findByMesaInAndEstadoNombre(List.of(), "ACTIVA"))
                    .thenReturn(List.of());

            List<MesaDTO> resultado = mesaService.listarMesas();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests para getMesa")
    class GetMesaTests {

        @Test
        @DisplayName("Debe retornar la mesa cuando el ID existe")
        void getMesa_cuandoExiste_retornaEntidadMesa() {
            when(mesaRepository.findById(100L)).thenReturn(Optional.of(mesa));

            Mesa resultado = mesaService.getMesa(100L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(100L);
            assertThat(resultado.getNumero()).isEqualTo(5);
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando el ID no existe")
        void getMesa_cuandoNoExiste_lanzaNoEncontradoException() {
            when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mesaService.getMesa(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Mesa no encontrada. Id: 999");
        }
    }

    @Nested
    @DisplayName("Tests para crearMesa")
    class CrearMesaTests {

        @Test
        @DisplayName("Debe crear la mesa con estado LIBRE, asociar la zona y publicar evento SSE")
        void crearMesa_datosValidos_creaMesaYPublicaEvento() {
            CrearMesaDTO dto = new CrearMesaDTO(5, 10L);

            when(mesaRepository.existsByNumero(5)).thenReturn(false);
            when(mesaEstadoRepository.findByNombre("LIBRE")).thenReturn(Optional.of(estadoLibre));
            when(zonaRepository.findById(10L)).thenReturn(Optional.of(zonaPrincipal));
            when(mesaRepository.save(any(Mesa.class))).thenAnswer(invocation -> {
                Mesa m = invocation.getArgument(0);
                m.setId(100L);
                return m;
            });

            MesaDTO resultado = mesaService.crearMesa(dto);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(100L);
            assertThat(resultado.numero()).isEqualTo(5);
            assertThat(resultado.estado()).isEqualTo("LIBRE");
            assertThat(resultado.zona()).isEqualTo("Terraza");
            assertThat(resultado.sesionActiva()).isNull();

            verify(mesaRepository).save(any(Mesa.class));
            verify(eventPublisher).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar RecursoExistenteException si el número de mesa ya existe")
        void crearMesa_numeroDuplicado_lanzaRecursoExistenteException() {
            CrearMesaDTO dto = new CrearMesaDTO(5, 10L);
            when(mesaRepository.existsByNumero(5)).thenReturn(true);

            assertThatThrownBy(() -> mesaService.crearMesa(dto))
                    .isInstanceOf(RecursoExistenteException.class)
                    .hasMessageContaining("La mesa ya existe");

            verify(mesaRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la zona especificada no existe")
        void crearMesa_zonaNoExiste_lanzaNoEncontradoException() {
            CrearMesaDTO dto = new CrearMesaDTO(5, 999L);
            when(mesaRepository.existsByNumero(5)).thenReturn(false);
            when(mesaEstadoRepository.findByNombre("LIBRE")).thenReturn(Optional.of(estadoLibre));
            when(zonaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mesaService.crearMesa(dto))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Zona no encontrada. Id: 999");

            verify(mesaRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Tests para cambiarEstado")
    class CambiarEstadoTests {

        @Test
        @DisplayName("Debe actualizar el estado de la mesa y publicar evento SSE")
        void cambiarEstado_mesaYEstadoValidos_actualizaEstado() {
            when(mesaRepository.findById(100L)).thenReturn(Optional.of(mesa));
            when(mesaEstadoRepository.findByNombre("OCUPADA")).thenReturn(Optional.of(estadoOcupada));
            when(mesaRepository.save(mesa)).thenReturn(mesa);

            MesaDTO resultado = mesaService.cambiarEstado(100L, "ocupada");

            assertThat(resultado).isNotNull();
            assertThat(resultado.estado()).isEqualTo("OCUPADA");
            assertThat(mesa.getEstado()).isEqualTo(estadoOcupada);

            verify(mesaRepository).save(mesa);
            verify(eventPublisher).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si el estado solicitado no existe en BD")
        void cambiarEstado_estadoInvalido_lanzaNoEncontradoException() {
            when(mesaRepository.findById(100L)).thenReturn(Optional.of(mesa));
            when(mesaEstadoRepository.findByNombre("ESTADO_INVENTADO")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mesaService.cambiarEstado(100L, "ESTADO_INVENTADO"))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Estado no encontrado");

            verify(mesaRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Tests para solicitarCuenta")
    class SolicitarCuentaTests {

        @Test
        @DisplayName("Debe cambiar el estado de la mesa a ESPERANDO_CUENTA cuando la sesión está ACTIVA")
        void solicitarCuenta_sesionActiva_actualizaMesaAEsperandoCuenta() {
            SesionEstado sesionActivaEstado = new SesionEstado();
            sesionActivaEstado.setNombre("ACTIVA");

            Sesion sesion = new Sesion();
            sesion.setId(200L);
            sesion.setQrCodeUrl("qr-code-test");
            sesion.setMesa(mesa);
            sesion.setEstado(sesionActivaEstado);

            when(sesionRepository.findByQrCodeUrl("qr-code-test")).thenReturn(Optional.of(sesion));
            when(mesaEstadoRepository.findByNombre("ESPERANDO_CUENTA")).thenReturn(Optional.of(estadoEsperandoCuenta));
            when(mesaRepository.save(mesa)).thenReturn(mesa);

            MesaDTO resultado = mesaService.solicitarCuenta("qr-code-test");

            assertThat(resultado).isNotNull();
            assertThat(resultado.estado()).isEqualTo("ESPERANDO_CUENTA");
            assertThat(resultado.sesionActiva()).isNotNull();
            assertThat(resultado.sesionActiva().qrCodeUrl()).isEqualTo("qr-code-test");
            assertThat(mesa.getEstado()).isEqualTo(estadoEsperandoCuenta);

            verify(mesaRepository).save(mesa);
            verify(eventPublisher).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la sesión no existe")
        void solicitarCuenta_sesionNoExiste_lanzaNoEncontradoException() {
            when(sesionRepository.findByQrCodeUrl("qr-invalido")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mesaService.solicitarCuenta("qr-invalido"))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Sesión no encontrada para el QR code");

            verify(mesaRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la sesión existe pero no está ACTIVA")
        void solicitarCuenta_sesionCerrada_lanzaNoEncontradoException() {
            SesionEstado sesionCerradaEstado = new SesionEstado();
            sesionCerradaEstado.setNombre("CERRADA");

            Sesion sesion = new Sesion();
            sesion.setId(200L);
            sesion.setQrCodeUrl("qr-cerrado");
            sesion.setMesa(mesa);
            sesion.setEstado(sesionCerradaEstado);

            when(sesionRepository.findByQrCodeUrl("qr-cerrado")).thenReturn(Optional.of(sesion));

            assertThatThrownBy(() -> mesaService.solicitarCuenta("qr-cerrado"))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("La sesión no está activa");

            verify(mesaRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Tests para eliminarMesa")
    class EliminarMesaTests {

        @Test
        @DisplayName("Debe eliminar la mesa por número y publicar evento SSE cuando existe")
        void eliminarMesa_cuandoExiste_eliminaMesaYPublicaEvento() {
            when(mesaRepository.existsByNumero(5)).thenReturn(true);

            mesaService.eliminarMesa(5);

            verify(mesaRepository).deleteByNumero(5);
            verify(eventPublisher).publishEvent(any(SseTopicEvent.class));
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando el número de mesa no existe")
        void eliminarMesa_cuandoNoExiste_lanzaNoEncontradoException() {
            when(mesaRepository.existsByNumero(99)).thenReturn(false);

            assertThatThrownBy(() -> mesaService.eliminarMesa(99))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Mesa no encontrada. Numero: 99");

            verify(mesaRepository, never()).deleteByNumero(99);
            verify(eventPublisher, never()).publishEvent(any());
        }
    }
}
