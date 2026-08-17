package com.easyorder.api.backend.model;

import java.math.BigDecimal;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido_item_modificador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItem_Modificador extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @TenantId
  @Column(name = "id_tenant", nullable = false)
  private Long tenantId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_pedido_item", nullable = false)
  private PedidoItem pedidoItem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_modificador", nullable = false)
  private Modificador modificador;

  @Column(nullable = false)
  @Builder.Default
  private Integer cantidad = 1;

  @Column(name = "precio_aplicado", nullable = false, precision = 10, scale = 2)
  private BigDecimal precioAplicado;
}