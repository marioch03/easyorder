package com.easyorder.api.backend.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoTipoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String nombre;
}