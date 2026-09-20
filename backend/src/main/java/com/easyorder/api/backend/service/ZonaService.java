package com.easyorder.api.backend.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.dto.ZonaDTO;
import com.easyorder.api.backend.repository.ZonaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZonaService {

    private static final String TENANT_KEY = "T(com.easyorder.api.backend.tenant.TenantContext).get()";

    private final ZonaRepository zonaRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "zonas", key = TENANT_KEY)
    public List<ZonaDTO> getZonas() {
        return zonaRepository.findAll().stream()
                .map(zona -> new ZonaDTO(zona.getId(), zona.getNombre()))
                .toList();
    }
}
