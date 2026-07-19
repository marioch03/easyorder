package com.easyorder.api.backend.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String nombre;
	private String descripcion;
	private double precio;
	private boolean disponible;
	private String imagen;
	private long tipoId;
}