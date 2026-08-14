package com.easyorder.api.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.dto.SesionAuthProjection;
import com.easyorder.api.backend.dto.SesionClienteDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.dto.SseTopic;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SesionService {

    private static final String TENANT_KEY = "T(com.easyorder.api.backend.tenant.TenantContext).get()";

    private final SesionRepository sesionRepository;
    private final SesionEstadoRepository sesionEstadoRepository;
    private final MesaRepository mesaRepository;
    private final MesaEstadoRepository mesaEstadoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<Sesion> listarSesiones() {
        return sesionRepository.findAll();
    }

    public Sesion save(Sesion sesion) {
        return sesionRepository.save(sesion);
    }

    @Transactional(readOnly = true)
    public Sesion getSesion(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Sesión no encontrada. Id: " + id));
    }

    public SesionEstado getEstadoSesion(String nombre) {
        return sesionEstadoRepository.findByNombre(nombre.toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado. Nombre: " + nombre));
    }

    @Transactional(readOnly = true)
    public boolean validarSesion(String sessionCode) {
        return sesionRepository.existsByQrCodeUrlAndEstadoNombreUnfiltered(sessionCode, "ACTIVA") > 0;
    }

    @Transactional
    public void eliminarSesion(Long id) {
        if (!sesionRepository.existsById(id)) {
            throw new NoEncontradoException("Sesión no encontrada. Id: " + id);
        }
        sesionRepository.deleteById(id);
    }

    @Transactional
    public Sesion crearSesion(Long idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + idMesa));

        SesionEstado estadoActiva = getEstadoSesion("ACTIVA");

        if (sesionRepository.findByMesaAndEstado(mesa, estadoActiva).isPresent()) {
            throw new RecursoExistenteException("La mesa ya tiene una sesión activa");
        }

        MesaEstado estadoOcupada = mesaEstadoRepository.findByNombre("OCUPADA")
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: OCUPADA"));
        mesa.setEstado(estadoOcupada);
        mesaRepository.save(mesa);

        String qrCodeUuid = UUID.randomUUID().toString();
        Sesion nuevaSesion = new Sesion(mesa, null, estadoActiva, qrCodeUuid);
        nuevaSesion.setHoraInicio(LocalDateTime.now());
        nuevaSesion = sesionRepository.save(nuevaSesion);

        eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.MESAS));
        return nuevaSesion;
    }

    @Transactional
    @CacheEvict(value = "sesionClienteCache", key = "#sessionCode")
    public Sesion cerrarSesion(String sessionCode) {
        Sesion sesion = getSesionPorCodigo(sessionCode);
        Mesa mesa = sesion.getMesa();

        MesaEstado estadoLibre = mesaEstadoRepository.findByNombre("LIBRE")
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: LIBRE"));
        mesa.setEstado(estadoLibre);
        mesaRepository.save(mesa);

        sesion.setHoraFin(LocalDateTime.now());
        sesion.setEstado(getEstadoSesion("FINALIZADA"));
        sesion = sesionRepository.save(sesion);

        eventPublisher.publishEvent(SseTopicEvent.of(SseTopic.MESAS));
        return sesion;
    }

    @Transactional(readOnly = true)
    public SesionDTO obtenerSesionActivaPorMesa(Long mesaId) {
        return sesionRepository.findByMesaIdAndEstadoNombre(mesaId, "ACTIVA")
                .map(sesion -> new SesionDTO(sesion.getId(), sesion.getQrCodeUrl()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Sesion getSesionPorCodigo(String qrCodeUrl) {
        return sesionRepository.findByQrCodeUrlUnfiltered(qrCodeUrl)
                .orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para el QR code: " + qrCodeUrl));
    }

    public Mesa getMesaPorCodigo(String qrCodeUrl) {
        return getSesionPorCodigo(qrCodeUrl).getMesa();
    }

    @Cacheable(value = "sesionClienteCache", key = TENANT_KEY + " + '::' + #sessionCode")
    @Transactional(readOnly = true)
    public SesionClienteDTO obtenerDatosSesionCliente(String sessionCode) {
        Sesion sesion = getSesionPorCodigo(sessionCode);
        Mesa mesa = sesion.getMesa();
        return new SesionClienteDTO(
                sesion.getId(),
                sesion.getQrCodeUrl(),
                mesa.getId(),
                mesa.getNumero(),
                mesa.getEstado().getNombre());
    }

    @Transactional(readOnly = true)
    public SesionAuthProjection getSesionActivaParaAutenticacion(String qrCodeUrl) {
        System.out.println("HA ENTRADOOOO");
        SesionAuthProjection sesion = sesionRepository.findAuthProjectionByQrCodeUrl(qrCodeUrl)
                .orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para el QR code: " + qrCodeUrl));

        if (!"ACTIVA".equals(sesion.getEstadoNombre())) {
            throw new NoEncontradoException("La sesión no está activa");
        }
        System.out.println(">>>>>>>>>>><SESSION: " + sesion.getEstadoNombre() + "<<<<<<<<<<");
        return sesion;
    }
}
