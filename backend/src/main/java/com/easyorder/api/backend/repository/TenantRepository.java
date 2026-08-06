package com.easyorder.api.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.easyorder.api.backend.model.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
  Optional<Tenant> findBySlugAndActivoTrue(String slug);

  boolean existsBySlug(String slug);

  @Query("SELECT t.id FROM Tenant t WHERE t.slug = :slug")
  Optional<Long> findIdBySlug(@Param("slug") String slug);
}
