package com.easyorder.api.backend.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.easyorder.api.backend.dto.ZonaDTO;
import com.easyorder.api.backend.model.Zona;
import com.easyorder.api.backend.repository.ZonaRepository;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class ZonaServiceTest {

    @Mock
    private ZonaRepository zonaRepository;

    @InjectMocks
    private ZonaService zonaService;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("Tests para getZonas")
    class GetZonasTests {

        @Test
        @DisplayName("Debe retornar la lista de zonas mapeadas a ZonaDTO")
        void getZonas_conZonasExistentes_retornaListaZonaDTO() {
            Zona zona1 = new Zona();
            zona1.setId(1L);
            zona1.setNombre("Terraza");
            zona1.setDescripcion("Zona exterior");

            Zona zona2 = new Zona();
            zona2.setId(2L);
            zona2.setNombre("Interior");
            zona2.setDescripcion("Salón climatizado");

            when(zonaRepository.findAll()).thenReturn(List.of(zona1, zona2));

            List<ZonaDTO> resultado = zonaService.getZonas();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0)).isEqualTo(new ZonaDTO(1L, "Terraza"));
            assertThat(resultado.get(1)).isEqualTo(new ZonaDTO(2L, "Interior"));
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay zonas configuradas")
        void getZonas_sinZonas_retornaListaVacia() {
            when(zonaRepository.findAll()).thenReturn(List.of());

            List<ZonaDTO> resultado = zonaService.getZonas();

            assertThat(resultado).isEmpty();
        }
    }
}
