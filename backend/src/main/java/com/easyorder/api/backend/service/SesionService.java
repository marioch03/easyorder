package com.easyorder.api.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.dto.SesionClienteDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.MesaEstado;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;
import com.easyorder.api.backend.repository.MesaEstadoRepository;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.SesionEstadoRepository;
import com.easyorder.api.backend.repository.SesionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final SesionEstadoRepository sesionEstadoRepository;
    private final MesaRepository mesaRepository;
    private final MesaEstadoRepository mesaEstadoRepository;

    private final WebSocketService webSocketService;

    public List<Sesion> listarSesiones() {
        return sesionRepository.findAll();
    }

    public Sesion save(Sesion sesion) {
        return sesionRepository.save(sesion);
    }

    public Sesion getSesion(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sesion no encontrada. ID: " + id));
    }

    public SesionEstado getEstadoSesion(String nombre) {
        return sesionEstadoRepository.findByNombre(nombre.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado. Nombre: " + nombre));
    }

    public boolean validarSesion(String sessionCode) {
        Optional<Sesion> sesion = sesionRepository.findByQrCodeUrl(sessionCode);
        if (sesion.isPresent()) {
            System.out.println(
                    "Sesión encontrada: " + sesion.get().getId() + ", estado: " + sesion.get().getEstado().getNombre());
        } else {
            System.out.println("Sesión no encontrada para el código: " + sessionCode);
        }
        if (sesion.isPresent() && sesion.get().getEstado().getNombre().equals("ACTIVA")) {
            return true;
        }
        return false;
    }

    public boolean validaSesion(Sesion sesion) {
        if (sesion.getEstado().getNombre().equals("ACTIVA")) {
            return true;
        }
        return false;
    }

    public void eliminarSesion(Long id) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Sesion no encontrada. Id: " + id));

        sesionRepository.delete(sesion);
    }

    public Sesion cambiarEstado(Long id, String estado) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Sesion no encontrada. Id: " + id));

        SesionEstado nuevoEstado = sesionEstadoRepository.findByNombre(estado.toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: " + estado));

        sesion.setEstado(nuevoEstado);
        return sesionRepository.save(sesion);
    }

    @Transactional
    public Sesion crearSesion(Long idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + idMesa));
        MesaEstado estadoOcupada = mesaEstadoRepository.findByNombre("OCUPADA")
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: OCUPADA"));
        mesa.setEstado(estadoOcupada);
        mesa = mesaRepository.save(mesa);
        SesionEstado estadoActiva = getEstadoSesion("ACTIVA");
        String qrCodeUuid = UUID.randomUUID().toString();
        Sesion nuevaSesion = new Sesion(mesa, null, estadoActiva, qrCodeUuid);
        nuevaSesion.setHoraInicio(LocalDateTime.now());
        nuevaSesion = sesionRepository.save(nuevaSesion);
        webSocketService.notifyMesaStateChange(obtenerMesasDTO());
        return nuevaSesion;
    }

    @CacheEvict(value = "sesionClienteCache", key = "#sessionCode")
    public Sesion cerrarSesion(String sessionCode) {
        Sesion sesion = getSesionPorCodigo(sessionCode);
        Mesa mesa = sesion.getMesa();
        MesaEstado estadoLibre = mesaEstadoRepository.findByNombre("LIBRE")
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: LIBRE"));
        mesa.setEstado(estadoLibre);
        mesa = mesaRepository.save(mesa);

        sesion.setHoraFin(LocalDateTime.now());
        sesion.setEstado(getEstadoSesion("FINALIZADA"));
        sesion = sesionRepository.save(sesion);
        webSocketService.notifyMesaStateChange(obtenerMesasDTO());
        return sesion;
    }

    public SesionDTO obtenerSesionActivaPorMesa(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + mesaId));
        SesionEstado estadoActiva = getEstadoSesion("ACTIVA");

        Optional<Sesion> sesionMesa = sesionRepository.findByMesaAndEstado(mesa, estadoActiva);
        if (sesionMesa.isPresent()) {
            Sesion sesion = sesionMesa.get();
            return new SesionDTO(
                    sesion.getId(),
                    sesion.getQrCodeUrl());
        } else {
            return null;
        }
    }

    public Sesion getSesionPorCodigo(String qrCodeUrl) {
        return sesionRepository.findByQrCodeUrl(qrCodeUrl)
                .orElseThrow(() -> new NoEncontradoException("Sesión no encontrada para el QR code: " + qrCodeUrl));
    }

    public Mesa getMesaPorCodigo(String qrCodeUrl) {
        Sesion sesion = getSesionPorCodigo(qrCodeUrl);
        return sesion.getMesa();
    }

    private List<MesaDTO> obtenerMesasDTO() {
        return mesaRepository.findAll().stream()
                .map(mesa -> new MesaDTO(
                        mesa.getId(),
                        mesa.getNumero(),
                        mesa.getEstado().getNombre(),
                        mesa.getZona().getNombre(),
                        obtenerSesionActiva(mesa)))
                .toList();
    }

    private SesionDTO obtenerSesionActiva(Mesa mesa) {
        SesionEstado estadoActiva = sesionEstadoRepository.findByNombre("ACTIVA")
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: ACTIVA"));
        Optional<Sesion> sesionMesa = sesionRepository.findByMesaAndEstado(mesa, estadoActiva);
        if (sesionMesa.isPresent()) {
            Sesion sesion = sesionMesa.get();
            return new SesionDTO(
                    sesion.getId(),
                    sesion.getQrCodeUrl());
        }
        return null;
    }

    @Cacheable(value = "sesionClienteCache", key = "#sessionCode")
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
}
