package com.easyorder.api.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.TenantId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_tenant_username", columnNames = { "id_tenant", "nombre" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TenantId
    @Column(name = "id_tenant", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String clave;

    @ManyToOne
    @JoinColumn(name = "rol", nullable = false)
    private UsuarioRol rol;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Token> tokens = new ArrayList<>();

    // --- Constructores personalizados ---

    public Usuario(String nombre, String clave, UsuarioRol rol, boolean activo) {
        this.nombre = nombre;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
    }

    public Usuario(Tenant tenant, String nombre, String clave, UsuarioRol rol, boolean activo) {
        this.tenantId = (tenant != null) ? tenant.getId() : null;
        this.nombre = nombre;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
    }

    public Usuario(Long tenantId, String nombre, String clave, UsuarioRol rol, boolean activo) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
    }
}