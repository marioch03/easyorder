package com.mario.tfg.backend.controller;

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

import com.mario.tfg.backend.dto.ClienteSolicitaCuentaRequest;
import com.mario.tfg.backend.dto.CrearMesaDTO;
import com.mario.tfg.backend.dto.MesaDTO;
import com.mario.tfg.backend.model.Mesa;
import com.mario.tfg.backend.service.MesaService;

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
    public ResponseEntity<Mesa> crearMesa(@Valid @RequestBody CrearMesaDTO crearMesaDTO) {
        Mesa mesaCreada = mesaService.crearMesa(crearMesaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaCreada);
    }

    @PutMapping("/admin/mesas/{id}/estado")
    public ResponseEntity<Mesa> cambiarEstadoMesa(@PathVariable Long id, @RequestParam String estado) {

        Mesa mesaActualizada = mesaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(mesaActualizada);
    }

    @PutMapping("/cliente/mesas/cuenta")
    public ResponseEntity<Mesa> clienteSolicitaCuenta(@RequestBody ClienteSolicitaCuentaRequest request,
            @RequestHeader("X-Session-Code") String sessionCode) {

        Mesa mesaActualizada = mesaService.cambiarEstadoCliente(request.mesaId(), request.estado(),
                sessionCode);
        return ResponseEntity.ok(mesaActualizada);
    }

    @DeleteMapping("/admin/mesas/{id}")
    public ResponseEntity<String> eliminarMesa(@PathVariable Long id) {
        try {
            mesaService.eliminarMesa(id);
            return ResponseEntity.ok("Mesa eliminada correctamente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
