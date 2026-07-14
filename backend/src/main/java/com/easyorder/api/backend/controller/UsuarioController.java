package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.UsuarioDTO;
import com.easyorder.api.backend.dto.UsuarioRolDTO;
import com.easyorder.api.backend.service.UsuarioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/admin/usuarios/list")
    public List<UsuarioDTO> getUsuarios() {
        return usuarioService.getAllUsers();
    }

    @PatchMapping("/admin/usuarios/{id}")
    public ResponseEntity<String> deshabilitarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deshabilitarUsuario(id);
            return ResponseEntity.ok("Usuario deshabilitado correctamente");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/admin/usuarios/roles")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public List<UsuarioRolDTO> getRoles() {
        return usuarioService.getAllRoles();
    }

}
