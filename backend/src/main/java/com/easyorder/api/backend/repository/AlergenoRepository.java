package com.easyorder.api.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Alergeno;

public interface AlergenoRepository extends JpaRepository<Alergeno, Long> {
}