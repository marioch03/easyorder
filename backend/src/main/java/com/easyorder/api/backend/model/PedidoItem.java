package com.easyorder.api.backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PedidoItem")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(length = 200)
    private String nota;

    @Column(name = "listo_para_servir")
    private boolean listoParaServir = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zona_trabajo")
    private ZonaTrabajo zonaTrabajo;

    @Column(nullable = true)
    private boolean servido = false;

    public PedidoItem() {
    }

    public PedidoItem(Producto producto, Pedido pedido, Integer cantidad, BigDecimal precioUnitario, String nota,
            boolean listoParaServir, ZonaTrabajo zonaTrabajo, boolean servido) {
        this.producto = producto;
        this.pedido = pedido;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.nota = nota;
        this.listoParaServir = listoParaServir;
        this.zonaTrabajo = zonaTrabajo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public boolean isListoParaServir() {
        return listoParaServir;
    }

    public void setListoParaServir(boolean listoParaServir) {
        this.listoParaServir = listoParaServir;
    }

    public ZonaTrabajo getZonaTrabajo() {
        return this.zonaTrabajo;
    }

    public void setZonaTrabajo(ZonaTrabajo zonaTrabajo) {
        this.zonaTrabajo = zonaTrabajo;
    }

    public boolean isServido() {
        return this.servido;
    }

    public void setServido(boolean servido) {
        this.servido = servido;
    }

    @Override
    public String toString() {
        return "PedidoItem{" +
                "id=" + id +
                ", producto=" + (producto != null ? producto.getNombre() : null) +
                ", pedidoId=" + (pedido != null ? pedido.getId() : null) +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", nota='" + nota + '\'' +
                ", listoParaServir=" + listoParaServir +
                '}';
    }
}