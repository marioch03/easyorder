package com.easyorder.api.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.dto.SesionClienteDTO;
import com.easyorder.api.backend.dto.SesionDTO;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.service.SesionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @PostMapping("/admin/sesiones/open/{idMesa}")
    public ResponseEntity<SesionDTO> crearSesion(@PathVariable Long idMesa) {
        Sesion nuevaSesion = sesionService.crearSesion(idMesa);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SesionDTO(nuevaSesion.getId(), nuevaSesion.getQrCodeUrl()));
    }

    @PostMapping("/admin/sesiones/close/{sessionCode}")
    public ResponseEntity<SesionDTO> cerrarSesion(@PathVariable String sessionCode) {
        Sesion sesion = sesionService.cerrarSesion(sessionCode);
        return ResponseEntity.ok(new SesionDTO(sesion.getId(), sesion.getQrCodeUrl()));
    }

    @GetMapping("/cliente/sesiones/mesa")
    public MesaDTO getMesaPorCodigo(@RequestHeader("X-Session-Code") String sessionCode) {
        Mesa mesa = sesionService.getMesaPorCodigo(sessionCode);
        String estado = mesa.getEstado() != null ? mesa.getEstado().getNombre() : null;
        String zona = mesa.getZona() != null ? mesa.getZona().getNombre() : null;
        return new MesaDTO(mesa.getId(), mesa.getNumero(), estado, zona, null);
    }

    @GetMapping("/cliente/sesiones/init")
    public SesionClienteDTO getDatosCliente(@RequestHeader("X-Session-Code") String sessionCode) {
        return sesionService.obtenerDatosSesionCliente(sessionCode);
    }

}
