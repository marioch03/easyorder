package com.easyorder.api.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.UsuarioDTO;
import com.easyorder.api.backend.dto.UsuarioRolDTO;
import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.repository.UsuarioRepository;
import com.easyorder.api.backend.repository.UsuarioRolRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public void deshabilitarUsuario(long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado. ID: " + id));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public List<UsuarioRolDTO> getAllRoles() {
        return usuarioRolRepository.findAll().stream().map(rol -> new UsuarioRolDTO(rol.getId(), rol.getNombre()))
                .toList();
    }

    public List<UsuarioDTO> getAllUsers() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.isActivo())).toList();
    }
}
