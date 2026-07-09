package com.mario.tfg.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mario.tfg.backend.dto.ZonaDTO;
import com.mario.tfg.backend.model.Zona;
import com.mario.tfg.backend.repository.ZonaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZonaService {

    private final ZonaRepository zonaRepository;

    public List<ZonaDTO> getZonas() {
        List<Zona> zonas = zonaRepository.findAll();
        List<ZonaDTO> zonasDTO = new ArrayList<>();
        for (Zona zona : zonas) {
            zonasDTO.add(new ZonaDTO(zona.getId(), zona.getNombre()));
        }
        return zonasDTO;
    }
}
