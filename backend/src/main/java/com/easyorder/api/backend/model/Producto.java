package com.easyorder.api.backend.model;

import java.math.BigDecimal;
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
@Table(name = "Producto", uniqueConstraints = {
        @UniqueConstraint(name = "uk_producto_tenant_nombre", columnNames = { "id_tenant", "nombre" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    @Builder.Default
    private boolean disponible = true;

    @Column(length = 200)
    private String imagen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo", nullable = false)
    private ProductoTipo tipo;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<PedidoItem> pedidoItems = new ArrayList<>();

    // --- Constructores personalizados ---

    public Producto(String nombre, String descripcion, BigDecimal precio, Boolean disponible, String imagen,
            ProductoTipo tipo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible != null ? disponible : true;
        this.imagen = imagen;
        this.tipo = tipo;
    }

    public Producto(Tenant tenant, String nombre, String descripcion, BigDecimal precio, Boolean disponible,
            String imagen,
            ProductoTipo tipo) {
        this.tenantId = (tenant != null) ? tenant.getId() : null;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible != null ? disponible : true;
        this.imagen = imagen;
        this.tipo = tipo;
    }

    public Producto(Long tenantId, String nombre, String descripcion, BigDecimal precio, Boolean disponible,
            String imagen,
            ProductoTipo tipo) {
        this.tenantId = tenantId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible != null ? disponible : true;
        this.imagen = imagen;
        this.tipo = tipo;
    }
}