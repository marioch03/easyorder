package com.easyorder.api.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_tenant_username", columnNames = {"id_tenant", "nombre"})
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tenant", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String clave;

    @ManyToOne
    @JoinColumn(name = "rol", nullable = false)
    private UsuarioRol rol;

    @Column(name = "created_at", columnDefinition = "datetime DEFAULT current_timestamp()")
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "usuario", fetch = jakarta.persistence.FetchType.LAZY)
    private java.util.List<Token> tokens;

    public Usuario(String nombre, String clave, UsuarioRol rol, Boolean activo) {
        this.nombre = nombre;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
    }

    public Usuario(Tenant tenant, String nombre, String clave, UsuarioRol rol, Boolean activo) {
        this.tenant = tenant;
        this.nombre = nombre;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public UsuarioRol getRol() {
        return rol;
    }

    public void setRol(UsuarioRol rol) {
        this.rol = rol;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public java.util.List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(java.util.List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", tenant=" + (tenant != null ? tenant.getId() : null) +
                ", nombre='" + nombre + '\'' +
                ", rol=" + (rol != null ? rol.getNombre() : null) +
                ", activo=" + activo +
                '}';
    }
}