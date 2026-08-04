package com.easyorder.api.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Tenant", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_slug", columnNames = {"slug"})
})
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 60, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Tenant() {
    }

    public Tenant(String nombre, String slug) {
        this.nombre = nombre;
        this.slug = slug;
        this.activo = true;
    }

    public Tenant(String nombre, String slug, Boolean activo) {
        this.nombre = nombre;
        this.slug = slug;
        this.activo = activo != null ? activo : true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", slug='" + slug + '\'' +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                '}';
    }
}
