package com.mario.tfg.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mario.tfg.backend.dto.CrearMesaDTO;
import com.mario.tfg.backend.dto.MesaDTO;
import com.mario.tfg.backend.dto.SesionDTO;
import com.mario.tfg.backend.exception.NoEncontradoException;
import com.mario.tfg.backend.exception.RecursoExistenteException;
import com.mario.tfg.backend.model.Mesa;
import com.mario.tfg.backend.model.MesaEstado;
import com.mario.tfg.backend.model.Sesion;
import com.mario.tfg.backend.model.SesionEstado;
import com.mario.tfg.backend.model.Zona;
import com.mario.tfg.backend.repository.MesaEstadoRepository;
import com.mario.tfg.backend.repository.MesaRepository;
import com.mario.tfg.backend.repository.SesionEstadoRepository;
import com.mario.tfg.backend.repository.SesionRepository;
import com.mario.tfg.backend.repository.ZonaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MesaService {

    private final WebSocketService webSocketService;

    private final MesaRepository mesaRepository;
    private final ZonaRepository zonaRepository;
    private final MesaEstadoRepository mesaEstadoRepository;
    private final SesionRepository sesionRepository;
    private final SesionEstadoRepository sesionEstadoRepository;

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
            SesionDTO sesionDTO = new SesionDTO();
            sesionDTO.setId(sesion.getId());
            sesionDTO.setQrCodeUrl(sesion.getQrCodeUrl());
            return sesionDTO;
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
        notificarCambios();
        return mesaCreada;
    }

    public void eliminarMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + id));

        mesaRepository.delete(mesa);
    }

    public Mesa cambiarEstado(Long id, String estado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Mesa no encontrada. Id: " + id));

        MesaEstado nuevoEstado = mesaEstadoRepository.findByNombre(estado.toUpperCase())
                .orElseThrow(() -> new NoEncontradoException("Estado no encontrado: " + estado));

        mesa.setEstado(nuevoEstado);
        Mesa mesaActualizada = mesaRepository.save(mesa);
        notificarCambios();
        return mesaActualizada;
    }

    public void notificarCambios() {
        List<MesaDTO> mesasDTO = listarMesas();
        webSocketService.notifyMesaStateChange(mesasDTO);
    }

    public Mesa cambiarEstadoCliente(Long mesaId, String estado, String sessionCode) {

        return cambiarEstado(mesaId, estado);
    }

}
