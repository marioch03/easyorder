package com.easyorder.api.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tenant", nullable = false)
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "id_sesion", nullable = false)
    private Sesion sesion;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private PedidoEstado estado;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PedidoItem> items = new HashSet<>();

    public Pedido() {
    }

    public Pedido(Sesion sesion, PedidoEstado estado) {
        this.sesion = sesion;
        this.estado = estado;
        this.createdAt = LocalDateTime.now();
    }

    public Pedido(Tenant tenant, Sesion sesion, PedidoEstado estado) {
        this.tenant = tenant;
        this.sesion = sesion;
        this.estado = estado;
        this.createdAt = LocalDateTime.now();
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

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public PedidoEstado getEstado() {
        return estado;
    }

    public void setEstado(PedidoEstado estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<PedidoItem> getItems() {
        return items;
    }

    public void setItems(Set<PedidoItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", tenant=" + (tenant != null ? tenant.getId() : null) +
                ", sesionId=" + (sesion != null ? sesion.getId() : null) +
                ", estado=" + (estado != null ? estado.getNombre() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}