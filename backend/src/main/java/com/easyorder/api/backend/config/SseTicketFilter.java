package com.easyorder.api.backend.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.easyorder.api.backend.dto.SseTicketData;
import com.easyorder.api.backend.service.SseTicketService;
import com.easyorder.api.backend.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseTicketFilter extends OncePerRequestFilter {

    private final SseTicketService sseTicketService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String ticket = request.getParameter("ticket");

        if (ticket != null && !ticket.isBlank()) {
            var ticketDataOpt = sseTicketService.redeemTicket(ticket);

            if (ticketDataOpt.isEmpty()) {
                log.warn("Intento de conexión SSE con ticket inválido o expirado");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/problem+json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write("""
                        {"type":"about:blank","title":"Unauthorized","status":401,"detail":"El ticket SSE no es válido, ha expirado o ya fue utilizado","message":"El ticket SSE no es válido, ha expirado o ya fue utilizado"}
                        """);
                return;
            }

            SseTicketData ticketData = ticketDataOpt.get();
            TenantContext.set(ticketData.tenantId());

            List<SimpleGrantedAuthority> authorities = ticketData.roles() != null
                    ? ticketData.roles().stream().map(SimpleGrantedAuthority::new).toList()
                    : List.of();

            var auth = new UsernamePasswordAuthenticationToken(ticketData.username(), null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            try {
                filterChain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/v1/sse/stream");
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
}
