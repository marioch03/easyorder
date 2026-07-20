package com.easyorder.api.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.CrearMesaDTO;
import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.dto.SseTopic;
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
import com.easyorder.api.backend.repository.SesionEstadoRepository;
import com.easyorder.api.backend.repository.SesionRepository;
import com.easyorder.api.backend.repository.ZonaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MesaService {

    private final MesaRepository mesaRepository;
    private final ZonaRepository zonaRepository;
    private final MesaEstadoRepository mesaEstadoRepository;
    private final SesionRepository sesionRepository;
    private final SesionEstadoRepository sesionEstadoRepository;

    private final ApplicationEventPublisher eventPublisher;

    public List<MesaDTO> listarMesas() {
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

    public Mesa save(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public Mesa getMesa(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + id));
    }

    public MesaEstado getEstadoMesa(String nombre) {
        return mesaEstadoRepository.findByNombre(nombre.toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado. Nombre: " + nombre));
    }

    public boolean existeMesa(int numero) {
        Optional<Mesa> mesa = mesaRepository.findByNumero(numero);
        if (mesa.isPresent()) {
            return true;
        }
        return false;
    }

    @Transactional
    public Mesa crearMesa(CrearMesaDTO crearMesaDTO) {
        if (existeMesa(crearMesaDTO.numero())) {
            throw new RecursoExistenteException("La mesa ya existe");
        }
        Mesa mesa = new Mesa();
        mesa.setNumero(crearMesaDTO.numero());
        MesaEstado estadoLibre = getEstadoMesa("LIBRE");
        mesa.setEstado(estadoLibre);
        Zona zona = zonaRepository.findById(crearMesaDTO.idZona())
                .orElseThrow(() -> new NoEncontradoException("Zona no encontrada. Id: " + crearMesaDTO.idZona()));
        mesa.setZona(zona);
        Mesa mesaCreada = save(mesa);
        eventPublisher.publishEvent(new SseTopicEvent(SseTopic.MESAS));
        return mesaCreada;
    }

    @Transactional
    public void eliminarMesa(int numero) {
        Mesa mesa = mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Numero: " + numero));

        mesaRepository.delete(mesa);
        eventPublisher.publishEvent(new SseTopicEvent(SseTopic.MESAS));
    }

    @Transactional
    public Mesa cambiarEstado(Long id, String estado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + id));

        MesaEstado nuevoEstado = mesaEstadoRepository.findByNombre(estado.toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: " + estado));

        mesa.setEstado(nuevoEstado);
        Mesa mesaActualizada = mesaRepository.save(mesa);
        eventPublisher.publishEvent(new SseTopicEvent(SseTopic.MESAS));
        return mesaActualizada;
    }

}
