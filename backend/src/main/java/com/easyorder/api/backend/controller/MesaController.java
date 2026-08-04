package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.CrearMesaDTO;
import com.easyorder.api.backend.dto.MesaDTO;
import com.easyorder.api.backend.service.MesaService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping("/admin/mesas/list")
    public List<MesaDTO> listarMesas() {
        return mesaService.listarMesas();
    }

    @PostMapping("/admin/mesas/create")
    public ResponseEntity<MesaDTO> crearMesa(@Valid @RequestBody CrearMesaDTO crearMesaDTO) {
        MesaDTO mesaCreada = mesaService.crearMesa(crearMesaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaCreada);
    }

    @PutMapping("/admin/mesas/{id}/estado")
    public ResponseEntity<MesaDTO> cambiarEstadoMesa(@PathVariable Long id, @RequestParam String estado) {
        MesaDTO mesaActualizada = mesaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(mesaActualizada);
    }

    @DeleteMapping("/admin/mesas/{numero}")
    public ResponseEntity<String> eliminarMesa(@PathVariable int numero) {
        try {
            mesaService.eliminarMesa(numero);
            return ResponseEntity.ok("Mesa eliminada correctamente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/cliente/mesas/cuenta")
    public ResponseEntity<MesaDTO> clienteSolicitaCuenta(@RequestHeader("X-Session-Code") String sessionCode) {
        MesaDTO mesaActualizada = mesaService.solicitarCuenta(sessionCode);
        return ResponseEntity.ok(mesaActualizada);
    }

}
