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

import com.easyorder.api.backend.service.SesionService;

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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String sessionCode = request.getHeader(SESSION_HEADER);

        if (sessionCode == null || sessionCode.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        final boolean sesionValida = sesionService.validarSesion(sessionCode);

        if (!sesionValida) {
            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid session code");
            return;
        }

        var authentication = new UsernamePasswordAuthenticationToken(
                sessionCode,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/cliente/");
    }
}