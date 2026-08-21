package com.easyorder.api.backend.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Importa tu repositorio/servicio de Tenant
import com.easyorder.api.backend.repository.TenantRepository;
import com.easyorder.api.backend.service.JwtService;
import com.easyorder.api.backend.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String tenantSlug = request.getHeader("X-Tenant-Slug");

            boolean tenantSet = false;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                final String jwt = authHeader.substring(7);
                try {
                    final Long tenantId = jwtService.extractTenantId(jwt);
                    if (tenantId != null) {
                        TenantContext.set(tenantId);
                        tenantSet = true;
                    }
                } catch (Exception e) {
                }
            }

            if (!tenantSet && tenantSlug != null && !tenantSlug.isEmpty()) {
                tenantRepository.findBySlugAndActivoTrue(tenantSlug).ifPresent(tenant -> {
                    TenantContext.set(tenant.getId());
                });
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }
}