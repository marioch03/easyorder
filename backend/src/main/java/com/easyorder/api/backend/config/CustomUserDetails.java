package com.easyorder.api.backend.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.easyorder.api.backend.model.Usuario;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

  private final Long id;
  private final Long tenantId;
  private final String username;
  private final String password;
  private final boolean enabled;
  private final Collection<? extends GrantedAuthority> authorities;

  public static CustomUserDetails fromUsuario(Usuario usuario) {
    return CustomUserDetails.builder()
        .id(usuario.getId())
        .tenantId(usuario.getTenantId())
        .username(usuario.getNombre())
        .password(usuario.getClave())
        .enabled(usuario.isActivo())
        .authorities(List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre())))
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }
}