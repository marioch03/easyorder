package com.mario.tfg.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mario.tfg.backend.model.Mesa;
import com.mario.tfg.backend.model.Sesion;
import com.mario.tfg.backend.model.SesionEstado;

public interface SesionRepository extends JpaRepository<Sesion, Long> {
    Optional<Sesion> findByMesaAndEstado(Mesa mesa, SesionEstado estado);

    Optional<Sesion> findByQrCodeUrl(String qrCodeUrl);
}
