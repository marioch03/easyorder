package com.easyorder.api.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.TenantId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
@Table(name = "producto_tipo", uniqueConstraints = {
        @UniqueConstraint(name = "uk_productotipo_tenant_nombre", columnNames = { "id_tenant", "nombre" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoTipo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TenantId
    @Column(name = "id_tenant", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zona_trabajo")
    private ZonaTrabajo zonaTrabajo;

    @OneToMany(mappedBy = "tipo")
    @JsonIgnore
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();

    public ProductoTipo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public ProductoTipo(Tenant tenant, String nombre, String descripcion) {
        this.tenantId = (tenant != null) ? tenant.getId() : null;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public ProductoTipo(Long tenantId, String nombre, String descripcion) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}