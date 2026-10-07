package com.easyorder.api.backend.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.easyorder.api.backend.dto.SseTopic;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseSecurityService {

    private static final Set<String> ROLES_KDS = Set.of("ADMIN", "KDS", "COCINA", "BARRA");
    private static final Set<String> ROLES_MESAS = Set.of("ADMIN", "PERSONAL");
    private static final Set<String> ROLES_PEDIDOS = Set.of("ADMIN", "PERSONAL", "KDS", "COCINA", "BARRA");

    public void validarAccesoTopic(SseTopic topic, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No se encuentra autenticado para suscribirse al canal SSE: " + topic.getValue());
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::normalizarRol)
                .collect(Collectors.toSet());

        boolean autorizado = switch (topic) {
            case KDS -> roles.stream().anyMatch(ROLES_KDS::contains);
            case MESAS -> roles.stream().anyMatch(ROLES_MESAS::contains);
            case PEDIDOS -> roles.stream().anyMatch(ROLES_PEDIDOS::contains);
        };

        if (!autorizado) {
            log.warn("Acceso denegado a topic SSE [{}]. Usuario: [{}], Roles: {}",
                    topic.getValue(), authentication.getName(), roles);
            throw new AccessDeniedException(
                    String.format("No dispone de los permisos necesarios para suscribirse al canal SSE '%s'", topic.getValue()));
        }
    }

    private String normalizarRol(String authority) {
        if (authority == null) {
            return "";
        }
        String clean = authority.trim().toUpperCase();
        if (clean.startsWith("ROLE_")) {
            return clean.substring(5);
        }
        return clean;
    }
}
