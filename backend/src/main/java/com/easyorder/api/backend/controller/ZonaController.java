package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.ZonaDTO;
import com.easyorder.api.backend.service.ZonaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final ZonaService zonaService;

    @GetMapping
    public List<ZonaDTO> getZonas() {
        return zonaService.getZonas();
    }
}
