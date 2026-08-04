package com.easyorder.api.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Mesa", uniqueConstraints = {
        @UniqueConstraint(name = "uk_mesa_tenant_numero", columnNames = {"id_tenant", "numero"})
})
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tenant", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private int numero;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private MesaEstado estado;

    @ManyToOne
    @JoinColumn(name = "id_zona")
    private Zona zona;

    public Mesa() {
    }

    public Mesa(int numero, MesaEstado estado, Zona zona) {
        this.numero = numero;
        this.estado = estado;
        this.zona = zona;
    }

    public Mesa(Tenant tenant, int numero, MesaEstado estado, Zona zona) {
        this.tenant = tenant;
        this.numero = numero;
        this.estado = estado;
        this.zona = zona;
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

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public MesaEstado getEstado() {
        return estado;
    }

    public void setEstado(MesaEstado estado) {
        this.estado = estado;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    @Override
    public String toString() {
        return "Mesa{" +
                "id=" + id +
                ", tenant=" + (tenant != null ? tenant.getId() : null) +
                ", numero=" + numero +
                ", estado=" + (estado != null ? estado.getNombre() : null) +
                ", zona=" + (zona != null ? zona.getNombre() : null) +
                '}';
    }
}