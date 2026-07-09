package com.easyorder.api.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.Sesion;
import com.easyorder.api.backend.model.SesionEstado;

public interface SesionRepository extends JpaRepository<Sesion, Long> {
    Optional<Sesion> findByMesaAndEstado(Mesa mesa, SesionEstado estado);

    Optional<Sesion> findByQrCodeUrl(String qrCodeUrl);
}
