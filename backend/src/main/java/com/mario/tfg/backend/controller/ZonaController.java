package com.mario.tfg.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mario.tfg.backend.dto.ZonaDTO;
import com.mario.tfg.backend.service.ZonaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final ZonaService zonaService;

    @GetMapping("/list")
    public List<ZonaDTO> getZonas() {
        return zonaService.getZonas();
    }
}
