package com.easyorder.api.backend.model;

/**
 * Distingue una declaración firme de alérgeno ("Contiene gluten") de un
 * aviso de precaución por posible contaminación cruzada ("Puede contener
 * trazas de frutos de cáscara"). Mapeado tal cual al ENUM de la columna
 * `tipo` en Producto_Alergeno.
 */
public enum AlergenoTipoEnum {
  CONTIENE,
  PUEDE_CONTENER_TRAZAS
}