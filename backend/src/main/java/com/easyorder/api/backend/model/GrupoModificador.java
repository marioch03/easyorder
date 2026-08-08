package com.easyorder.api.backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GrupoModificador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoModificador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(name = "seleccion_minima", nullable = false)
  @Builder.Default
  private Integer seleccionMinima = 0;

  @Column(name = "seleccion_maxima", nullable = false)
  @Builder.Default
  private Integer seleccionMaxima = 1;

  @Column(nullable = false)
  @Builder.Default
  private Boolean activo = true;

  @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Modificador> modificadores = new ArrayList<>();
}
