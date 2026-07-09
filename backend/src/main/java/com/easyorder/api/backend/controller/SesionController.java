package com.easyorder.api.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.service.SesionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @PostMapping("/admin/sesiones/create/{idMesa}")
    public ResponseEntity<Sesion> crearSesion(@PathVariable Long idMesa) {
        Sesion nuevaSesion = sesionService.crearSesion(idMesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSesion);
    }

    @PostMapping("/admin/sesiones/close/{idMesa}")
    public ResponseEntity<Sesion> cerrarSesion(@PathVariable Long idMesa) {
        Sesion sesion = sesionService.cerrarSesion(idMesa);
        return ResponseEntity.ok(sesion);
    }

    @PutMapping("/admin/sesiones/{id}/estado")
    public ResponseEntity<Sesion> cambiarEstadoSesion(@PathVariable Long id, @RequestParam String estado) {

        Sesion sesionActualizada = sesionService.cambiarEstado(id, estado);
        return ResponseEntity.ok(sesionActualizada);
    }

    @GetMapping("/cliente/sesiones/mesa")
    public Mesa getMesaPorCodigo(@RequestHeader("X-Session-Code") String sessionCode) {
        return sesionService.getMesaPorCodigo(sessionCode);
    }

}
