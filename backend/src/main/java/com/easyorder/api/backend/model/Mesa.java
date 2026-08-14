package com.easyorder.api.backend.model;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mesa", uniqueConstraints = {
        @UniqueConstraint(name = "uk_mesa_tenant_numero", columnNames = { "id_tenant", "numero" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TenantId
    @Column(name = "id_tenant", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private int numero;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private MesaEstado estado;

    @ManyToOne
    @JoinColumn(name = "id_zona")
    private Zona zona;

}