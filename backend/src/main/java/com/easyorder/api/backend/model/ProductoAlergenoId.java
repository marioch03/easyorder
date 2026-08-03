package com.easyorder.api.backend.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProductoAlergenoId implements Serializable {

  private Long idProducto;
  private Long idAlergeno;

  public ProductoAlergenoId() {
  }

  public ProductoAlergenoId(Long idProducto, Long idAlergeno) {
    this.idProducto = idProducto;
    this.idAlergeno = idAlergeno;
  }

  public Long getIdProducto() {
    return idProducto;
  }

  public void setIdProducto(Long idProducto) {
    this.idProducto = idProducto;
  }

  public Long getIdAlergeno() {
    return idAlergeno;
  }

  public void setIdAlergeno(Long idAlergeno) {
    this.idAlergeno = idAlergeno;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ProductoAlergenoId that))
      return false;
    return Objects.equals(idProducto, that.idProducto) && Objects.equals(idAlergeno, that.idAlergeno);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idProducto, idAlergeno);
  }
}