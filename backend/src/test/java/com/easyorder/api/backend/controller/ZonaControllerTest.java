package com.easyorder.api.backend.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.easyorder.api.backend.dto.ZonaDTO;
import com.easyorder.api.backend.exception.GlobalExceptionHandler;
import com.easyorder.api.backend.service.ZonaService;

@ExtendWith(MockitoExtension.class)
class ZonaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ZonaService zonaService;

    @InjectMocks
    private ZonaController zonaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(zonaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /admin/zonas - Debe retornar HTTP 200 y la lista de zonas")
    void getZonas_retornaListaZonasYStatus200() throws Exception {
        ZonaDTO zona1 = new ZonaDTO(1L, "Terraza");
        ZonaDTO zona2 = new ZonaDTO(2L, "Interior");

        when(zonaService.getZonas()).thenReturn(List.of(zona1, zona2));

        mockMvc.perform(get("/admin/zonas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Terraza"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("Interior"));
    }
}
