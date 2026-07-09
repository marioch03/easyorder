package com.easyorder.api.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    public Optional<UsuarioRol> findByNombre(String nombre);

}
