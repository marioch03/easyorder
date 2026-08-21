package com.easyorder.api.backend.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.easyorder.api.backend.service.CustomUserDetailsService;
import com.easyorder.api.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        log.debug("Processing JWT token for request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            if (!jwtService.isValidAndNotRevoked(jwt)) {
                log.debug("JWT token validation failed for request: {} {}", request.getMethod(),
                        request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter()
                        .write("{\"error\":\"RefreshToken expired or invalid\",\"code\":\"TOKEN_EXPIRED\"}");
                return;
            }

            final String subject = jwtService.getSubject(jwt);
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(subject);
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("JWT authentication successful for user: {}", subject);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT token validation error for request: {} {}: {}",
                    request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"RefreshToken validation failed\",\"code\":\"TOKEN_INVALID\"}");
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
}
