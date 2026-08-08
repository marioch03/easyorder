package com.easyorder.api.backend.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Producto_GrupoModificadorId implements Serializable {

  @Column(name = "id_producto")
  private Long idProducto;

  @Column(name = "id_grupo")
  private Long idGrupo;
}