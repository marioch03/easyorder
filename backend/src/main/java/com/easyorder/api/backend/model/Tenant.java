package com.easyorder.api.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tenant", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_slug", columnNames = { "slug" })
})
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Tenant extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 60, unique = true)
    private String slug;

    @Column(nullable = false)
    private Boolean activo = true;

    public Tenant(String nombre, String slug) {
        this.nombre = nombre;
        this.slug = slug;
        this.activo = true;
    }

    public Tenant(String nombre, String slug, Boolean activo) {
        this.nombre = nombre;
        this.slug = slug;
        this.activo = activo != null ? activo : true;
    }
}