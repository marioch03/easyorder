package com.easyorder.api.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto_grupo_modificador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto_GrupoModificador extends AuditableEntity {

  @EmbeddedId
  private Producto_GrupoModificadorId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("idProducto")
  @JoinColumn(name = "id_producto")
  private Producto producto;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("idGrupo")
  @JoinColumn(name = "id_grupo")
  private GrupoModificador grupo;

  @Column(name = "orden_visual")
  @Builder.Default
  private Integer ordenVisual = 0;
}
