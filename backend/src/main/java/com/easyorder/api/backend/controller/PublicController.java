package com.easyorder.api.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyorder.api.backend.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

  private final TenantRepository tenantRepository;

  @GetMapping("/tenant/exists/{slug}")
  public ResponseEntity<Void> checkTenantExists(@PathVariable String slug) {
    boolean exists = tenantRepository.existsBySlug(slug);
    if (exists) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
