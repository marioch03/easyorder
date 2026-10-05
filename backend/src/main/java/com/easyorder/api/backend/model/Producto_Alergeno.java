package com.easyorder.api.backend.model;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto_alergeno")
@Getter
@Setter
@NoArgsConstructor
public class Producto_Alergeno {

  @EmbeddedId
  private ProductoAlergenoId id = new ProductoAlergenoId();

  @TenantId
  @Column(name = "id_tenant", nullable = false)
  private Long tenantId;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("idProducto")
  @JoinColumn(name = "id_producto", nullable = false)
  private Producto producto;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("idAlergeno")
  @JoinColumn(name = "id_alergeno", nullable = false)
  private Alergeno alergeno;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private AlergenoTipoEnum tipo = AlergenoTipoEnum.CONTIENE;

  public Producto_Alergeno(Producto producto, Alergeno alergeno, AlergenoTipoEnum tipo) {
    this.producto = producto;
    this.alergeno = alergeno;
    this.tipo = tipo;
    this.tenantId = (producto != null) ? producto.getTenantId() : null;
    this.id = new ProductoAlergenoId(
        (producto != null) ? producto.getId() : null,
        (alergeno != null) ? alergeno.getId() : null);
  }
}