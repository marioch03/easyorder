package com.easyorder.api.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.dto.UsuarioDTO;
import com.easyorder.api.backend.dto.UsuarioRolDTO;
import com.easyorder.api.backend.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/admin/usuarios")
    public List<UsuarioDTO> getUsuarios() {
        return usuarioService.getAllUsers();
    }

    @PatchMapping("/admin/usuarios/{id}")
    public ResponseEntity<Void> deshabilitarUsuario(@PathVariable Long id) {
        usuarioService.deshabilitarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/usuarios/roles")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public List<UsuarioRolDTO> getRoles() {
        return usuarioService.getAllRoles();
    }

}
