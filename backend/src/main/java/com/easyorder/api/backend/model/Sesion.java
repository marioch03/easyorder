package com.easyorder.api.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "Sesion")
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tenant", nullable = false)
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private SesionEstado estado;

    @Column(name = "hora_inicio", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin")
    private LocalDateTime horaFin;

    @Column(name = "qr_code_url", length = 50)
    private String qrCodeUrl;

    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Pedido> pedidos = new HashSet<>();

    public Sesion() {
    }

    public Sesion(Mesa mesa, Usuario usuario, SesionEstado estado, String qrCodeUrl) {
        this.mesa = mesa;
        this.usuario = usuario;
        this.estado = estado;
        this.qrCodeUrl = qrCodeUrl;
    }

    public Sesion(Tenant tenant, Mesa mesa, Usuario usuario, SesionEstado estado, String qrCodeUrl) {
        this.tenant = tenant;
        this.mesa = mesa;
        this.usuario = usuario;
        this.estado = estado;
        this.qrCodeUrl = qrCodeUrl;
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

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public SesionEstado getEstado() {
        return estado;
    }

    public void setEstado(SesionEstado estado) {
        this.estado = estado;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        this.horaFin = horaFin;
    }

    public Set<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Set<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    @Override
    public String toString() {
        return "Sesion{" +
                "id=" + id +
                ", tenant=" + (tenant != null ? tenant.getId() : null) +
                ", mesa=" + (mesa != null ? mesa.getNumero() : null) +
                ", usuario=" + (usuario != null ? usuario.getNombre() : null) +
                ", estado=" + (estado != null ? estado.getNombre() : null) +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", qrCodeUrl=" + qrCodeUrl +
                ", pedidosCount=" + (pedidos != null ? pedidos.size() : 0) +
                '}';
    }
}