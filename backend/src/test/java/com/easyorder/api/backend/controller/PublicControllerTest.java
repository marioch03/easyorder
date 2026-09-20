package com.easyorder.api.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.repository.TenantRepository;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private PublicController publicController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /public/tenant/exists/{slug} debe retornar HTTP 200 cuando el tenant existe")
    void checkTenantExists_cuandoExiste_retorna200() throws Exception {
        when(tenantRepository.existsBySlug("bar-central")).thenReturn(true);

        mockMvc.perform(get("/public/tenant/exists/bar-central"))
                .andExpect(status().isOk());

        verify(tenantRepository).existsBySlug("bar-central");
    }

    @Test
    @DisplayName("GET /public/tenant/exists/{slug} debe retornar HTTP 404 cuando el tenant no existe")
    void checkTenantExists_cuandoNoExiste_retorna404() throws Exception {
        when(tenantRepository.existsBySlug("bar-desconocido")).thenReturn(false);

        mockMvc.perform(get("/public/tenant/exists/bar-desconocido"))
                .andExpect(status().isNotFound());

        verify(tenantRepository).existsBySlug("bar-desconocido");
    }
}
