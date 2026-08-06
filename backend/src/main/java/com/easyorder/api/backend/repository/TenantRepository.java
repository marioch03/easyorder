package com.easyorder.api.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyorder.api.backend.model.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
  Optional<Tenant> findBySlugAndActivoTrue(String slug);
}
