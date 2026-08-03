package com.easyorder.api.backend.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.easyorder.api.backend.model.Zona;
import com.easyorder.api.backend.repository.MesaEstadoRepository;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.SesionRepository;
import com.easyorder.api.backend.repository.ZonaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final ZonaRepository zonaRepository;
    private final MesaEstadoRepository mesaEstadoRepository;
    private final SesionRepository sesionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<MesaDTO> listarMesas() {
        List<Mesa> mesas = mesaRepository.findAll();

        Map<Long, Sesion> sesionActivaPorMesa = sesionRepository
                .findByMesaInAndEstadoNombre(mesas, "ACTIVA")
                .stream()
                .collect(Collectors.toMap(s -> s.getMesa().getId(), Function.identity()));

        return mesas.stream()
                .map(mesa -> new MesaDTO(
                        mesa.getId(),
                        mesa.getNumero(),
                        mesa.getEstado().getNombre(),
                        mesa.getZona().getNombre(),
                        toSesionDTO(sesionActivaPorMesa.get(mesa.getId()))))
                .toList();
    }

    @Transactional(readOnly = true)
    public Mesa getMesa(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + id));
    }

    @Transactional
    public Mesa crearMesa(CrearMesaDTO crearMesaDTO) {
        if (existeMesa(crearMesaDTO.numero())) {
            throw new RecursoExistenteException("La mesa ya existe");
        }

        Mesa mesa = new Mesa();
        mesa.setNumero(crearMesaDTO.numero());
        mesa.setEstado(getEstadoMesa("LIBRE"));

        Zona zona = zonaRepository.findById(crearMesaDTO.idZona())
                .orElseThrow(() -> new NoEncontradoException("Zona no encontrada. Id: " + crearMesaDTO.idZona()));
        mesa.setZona(zona);

        Mesa mesaCreada = save(mesa);
        eventPublisher.publishEvent(new SseTopicEvent(SseTopic.MESAS));
        return mesaCreada;
    }

    @Transactional
    public Mesa cambiarEstado(Long id, String estado) {
        Mesa mesa = getMesa(id);
        MesaEstado nuevoEstado = getEstadoMesa(estado);

        mesa.setEstado(nuevoEstado);

        Mesa mesaActualizada = save(mesa);
        eventPublisher.publishEvent(new SseTopicEvent(SseTopic.MESAS));
        return mesaActualizada;
    }

    @Transactional
    public void eliminarMesa(int numero) {
        if (!existeMesa(numero)) {
            throw new NoEncontradoException("Mesa no encontrada. Numero: " + numero);
        }

        mesaRepository.deleteByNumero(numero);
        eventPublisher.publishEvent(new SseTopicEvent(SseTopic.MESAS));
    }

    public Mesa save(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public boolean existeMesa(int numero) {
        return mesaRepository.existsByNumero(numero);
    }

    public MesaEstado getEstadoMesa(String nombre) {
        return mesaEstadoRepository.findByNombre(nombre.toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado. Nombre: " + nombre));
    }

    private SesionDTO toSesionDTO(Sesion sesion) {
        return sesion == null ? null : new SesionDTO(sesion.getId(), sesion.getQrCodeUrl());
    }
}