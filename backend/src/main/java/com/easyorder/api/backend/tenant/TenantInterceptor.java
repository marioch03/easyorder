package com.easyorder.api.backend.tenant;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.repository.TenantRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

  private final TenantRepository tenantRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    String tenantSlug = request.getHeader("X-Tenant-Slug");

    if (tenantSlug != null && !tenantSlug.isEmpty()) {
      Long tenantId = tenantRepository.findIdBySlug(tenantSlug)
          .orElseThrow(() -> new NoEncontradoException("El tenant con slug " + tenantSlug + " no existe"));

      TenantContext.set(tenantId);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return false;
    }

    return true;
  }
}
