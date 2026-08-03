package com.easyorder.api.backend.dto;

import com.easyorder.api.backend.model.AlergenoTipoEnum;

public record AlergenoDTO(String nombre, AlergenoTipoEnum tipo) {
}
