package com.mario.tfg.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    public Optional<UsuarioRol> findByNombre(String nombre);

}
