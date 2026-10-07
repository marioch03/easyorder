package com.easyorder.api.backend.config;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.easyorder.api.backend.dto.SesionAuthProjection;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.service.SesionService;
import com.easyorder.api.backend.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionCodeFilter extends OncePerRequestFilter {

    private static final String SESSION_HEADER = "X-Session-Code";
    private final SesionService sesionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String sessionCode = request.getHeader(SESSION_HEADER);
        if ((sessionCode == null || sessionCode.isBlank())
                && (request.getServletPath().startsWith("/sse/") || request.getServletPath().startsWith("/api/v1/sse/"))) {
            sessionCode = request.getParameter("sessionCode");
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        final SesionAuthProjection sesion;
        try {
            sesion = sesionService.getSesionActivaParaAutenticacion(sessionCode);
        } catch (NoEncontradoException e) {
            if (request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION) != null) {
                filterChain.doFilter(request, response);
                return;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/problem+json");
            response.getWriter().write("""
                    {"type":"about:blank","title":"Unauthorized","status":401,"detail":"Invalid session code","message":"Invalid session code"}
                    """);
            return;
        }

        try {
            TenantContext.set(sesion.getTenantId());
            var authentication = new UsernamePasswordAuthenticationToken(
                    sessionCode, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/cliente/") && !path.startsWith("/sse/") && !path.startsWith("/api/v1/sse/");
    }
}