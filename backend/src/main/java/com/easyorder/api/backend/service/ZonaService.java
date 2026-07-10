package com.easyorder.api.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.ZonaDTO;
import com.easyorder.api.backend.model.Zona;
import com.easyorder.api.backend.repository.ZonaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZonaService {

    private final ZonaRepository zonaRepository;

    @Cacheable(value = "zonas", key = "'zonas'")
    public List<ZonaDTO> getZonas() {
        List<Zona> zonas = zonaRepository.findAll();
        List<ZonaDTO> zonasDTO = new ArrayList<>();
        for (Zona zona : zonas) {
            zonasDTO.add(new ZonaDTO(zona.getId(), zona.getNombre()));
        }
        return zonasDTO;
    }
}
