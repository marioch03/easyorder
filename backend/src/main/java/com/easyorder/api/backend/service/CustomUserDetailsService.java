package com.easyorder.api.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyorder.api.backend.config.CustomUserDetails;
import com.easyorder.api.backend.model.Usuario;
import com.easyorder.api.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Usuario usuario = usuarioRepository.findByNombre(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

    return CustomUserDetails.fromUsuario(usuario);
  }
}
