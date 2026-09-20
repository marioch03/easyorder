package com.easyorder.api.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.easyorder.api.backend.dto.EditarProductoDTO;
import com.easyorder.api.backend.dto.ProductoComandaDTO;
import com.easyorder.api.backend.dto.ProductoDTO;
import com.easyorder.api.backend.dto.ProductoTipoDTO;
import com.easyorder.api.backend.exception.NoEncontradoException;
import com.easyorder.api.backend.model.Alergeno;
import com.easyorder.api.backend.model.AlergenoTipoEnum;
import com.easyorder.api.backend.model.GrupoModificador;
import com.easyorder.api.backend.model.Modificador;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.ProductoTipo;
import com.easyorder.api.backend.model.Producto_Alergeno;
import com.easyorder.api.backend.model.Producto_GrupoModificador;
import com.easyorder.api.backend.model.ZonaTrabajo;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.ProductoTipoRepository;
import com.easyorder.api.backend.repository.Producto_AlergenoRepository;
import com.easyorder.api.backend.repository.Producto_GrupoModificadorRepository;
import com.easyorder.api.backend.repository.ZonaTrabajoRepository;
import com.easyorder.api.backend.tenant.TenantContext;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoTipoRepository productoTipoRepository;

    @Mock
    private ZonaTrabajoRepository zonaTrabajoRepository;

    @Mock
    private Producto_AlergenoRepository productoAlergenoRepository;

    @Mock
    private Producto_GrupoModificadorRepository productoGrupoModificadorRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoTipo tipoBebida;
    private ProductoTipo tipoComida;
    private Producto producto;
    private ZonaTrabajo zonaCocina;

    @BeforeEach
    void setUp() {
        TenantContext.set(1L);

        zonaCocina = new ZonaTrabajo("COCINA");
        zonaCocina.setId(1L);

        tipoComida = new ProductoTipo("Hamburguesas", "Hamburguesas caseras");
        tipoComida.setId(10L);
        tipoComida.setZonaTrabajo(zonaCocina);

        tipoBebida = new ProductoTipo("Bebidas", "Refrescos y aguas");
        tipoBebida.setId(20L);

        producto = new Producto();
        producto.setId(100L);
        producto.setTenantId(1L);
        producto.setNombre("Burger Clásica");
        producto.setDescripcion("Con queso y lechuga");
        producto.setPrecio(new BigDecimal("12.50"));
        producto.setDisponible(true);
        producto.setImagen("burger.jpg");
        producto.setTipo(tipoComida);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("Tests para findAll")
    class FindAllTests {

        @Test
        @DisplayName("Debe listar productos enriqueciendo alérgenos y grupos de modificadores en lote")
        void findAll_conProductos_retornaListaProductoDTO() {
            Alergeno gluten = new Alergeno();
            gluten.setId(1L);
            gluten.setNombre("Gluten");

            Producto_Alergeno pa = new Producto_Alergeno();
            pa.setProducto(producto);
            pa.setAlergeno(gluten);
            pa.setTipo(AlergenoTipoEnum.CONTIENE);

            Modificador extraQueso = new Modificador();
            extraQueso.setId(1L);
            extraQueso.setNombre("Extra Queso");
            extraQueso.setPrecioExtra(new BigDecimal("1.50"));

            GrupoModificador grupo = new GrupoModificador();
            grupo.setId(5L);
            grupo.setNombre("Extras");
            grupo.setSeleccionMinima(0);
            grupo.setSeleccionMaxima(3);
            grupo.setModificadores(List.of(extraQueso));

            Producto_GrupoModificador pgm = new Producto_GrupoModificador();
            pgm.setProducto(producto);
            pgm.setGrupo(grupo);

            when(productoRepository.findAll()).thenReturn(List.of(producto));
            when(productoAlergenoRepository.findByProducto_IdIn(List.of(100L))).thenReturn(List.of(pa));
            when(productoGrupoModificadorRepository.findByProducto_IdIn(List.of(100L))).thenReturn(List.of(pgm));

            List<ProductoDTO> resultado = productoService.findAll();

            assertThat(resultado).hasSize(1);
            ProductoDTO dto = resultado.get(0);
            assertThat(dto.getId()).isEqualTo(100L);
            assertThat(dto.getNombre()).isEqualTo("Burger Clásica");
            assertThat(dto.getPrecio()).isEqualTo(12.50);
            assertThat(dto.getTipoId()).isEqualTo(10L);
            assertThat(dto.getAlergenos()).hasSize(1);
            assertThat(dto.getAlergenos().get(0).nombre()).isEqualTo("Gluten");
            assertThat(dto.getGruposModificadores()).hasSize(1);
            assertThat(dto.getGruposModificadores().get(0).nombre()).isEqualTo("Extras");
            assertThat(dto.getGruposModificadores().get(0).modificadores()).hasSize(1);
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no hay productos")
        void findAll_sinProductos_retornaVacio() {
            when(productoRepository.findAll()).thenReturn(List.of());
            when(productoAlergenoRepository.findByProducto_IdIn(List.of())).thenReturn(List.of());
            when(productoGrupoModificadorRepository.findByProducto_IdIn(List.of())).thenReturn(List.of());

            List<ProductoDTO> resultado = productoService.findAll();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests para getProductosSimplificados")
    class GetProductosSimplificadosTests {

        @Test
        @DisplayName("Debe retornar la lista simplificada para comandas")
        void getProductosSimplificados_conProductos_retornaListaComandaDTO() {
            when(productoRepository.findAll()).thenReturn(List.of(producto));
            when(productoGrupoModificadorRepository.findByProducto_IdIn(List.of(100L))).thenReturn(List.of());

            List<ProductoComandaDTO> resultado = productoService.getProductosSimplificados();

            assertThat(resultado).hasSize(1);
            ProductoComandaDTO dto = resultado.get(0);
            assertThat(dto.id()).isEqualTo(100L);
            assertThat(dto.nombre()).isEqualTo("Burger Clásica");
            assertThat(dto.precio()).isEqualTo(new BigDecimal("12.50"));
            assertThat(dto.idTipoProducto()).isEqualTo(10L);
            assertThat(dto.disponible()).isTrue();
        }
    }

    @Nested
    @DisplayName("Tests para getProducto")
    class GetProductoTests {

        @Test
        @DisplayName("Debe retornar el producto cuando existe el ID")
        void getProducto_cuandoExiste_retornaProducto() {
            when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));

            Producto resultado = productoService.getProducto(100L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(100L);
            assertThat(resultado.getNombre()).isEqualTo("Burger Clásica");
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando no existe el ID")
        void getProducto_cuandoNoExiste_lanzaNoEncontradoException() {
            when(productoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productoService.getProducto(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Producto no encontrado. ID: 999");
        }
    }

    @Nested
    @DisplayName("Tests para deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("Debe eliminar el producto cuando existe")
        void deleteById_cuandoExiste_eliminaProducto() {
            when(productoRepository.existsById(100L)).thenReturn(true);

            productoService.deleteById(100L);

            verify(productoRepository).deleteById(100L);
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException cuando el producto a eliminar no existe")
        void deleteById_cuandoNoExiste_lanzaNoEncontradoException() {
            when(productoRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> productoService.deleteById(999L))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Producto no encontrado. ID: 999");
        }
    }

    @Nested
    @DisplayName("Tests para editarProducto")
    class EditarProductoTests {

        @Test
        @DisplayName("Debe actualizar las propiedades del producto y asociar el nuevo tipo")
        void editarProducto_datosValidos_actualizaProducto() {
            EditarProductoDTO dto = new EditarProductoDTO(100L, "Burger Doble", "Con doble carne", 15.0, 20L, true);

            when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
            when(productoTipoRepository.findById(20L)).thenReturn(Optional.of(tipoBebida));
            when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

            EditarProductoDTO resultado = productoService.editarProducto(100L, dto);

            assertThat(resultado).isNotNull();
            assertThat(resultado.nombre()).isEqualTo("Burger Doble");
            assertThat(resultado.precio()).isEqualTo(15.0);
            assertThat(resultado.tipoId()).isEqualTo(20L);

            verify(productoRepository).save(producto);
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si el producto a editar no existe")
        void editarProducto_productoNoExiste_lanzaNoEncontradoException() {
            EditarProductoDTO dto = new EditarProductoDTO(999L, "Burger", "Desc", 10.0, 10L, true);

            when(productoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productoService.editarProducto(999L, dto))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("Producto no encontrado. ID: 999");
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si el tipo de producto asignado no existe")
        void editarProducto_tipoNoExiste_lanzaNoEncontradoException() {
            EditarProductoDTO dto = new EditarProductoDTO(100L, "Burger", "Desc", 10.0, 999L, true);

            when(productoRepository.findById(100L)).thenReturn(Optional.of(producto));
            when(productoTipoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productoService.editarProducto(100L, dto))
                    .isInstanceOf(NoEncontradoException.class)
                    .hasMessageContaining("ProductoTipo no encontrado. ID: 999");
        }
    }

    @Nested
    @DisplayName("Tests para getTipos y getTiposKds")
    class TiposTests {

        @Test
        @DisplayName("Debe retornar todos los tipos de productos")
        void getTipos_retornaListaTipos() {
            when(productoTipoRepository.findAll()).thenReturn(List.of(tipoComida, tipoBebida));

            List<ProductoTipoDTO> resultado = productoService.getTipos();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Hamburguesas");
            assertThat(resultado.get(1).getNombre()).isEqualTo("Bebidas");
        }

        @Test
        @DisplayName("Debe retornar los tipos filtrados por zona de trabajo para KDS")
        void getTiposKds_zonaValida_retornaTiposDeLaZona() {
            when(zonaTrabajoRepository.findByNombre("COCINA")).thenReturn(Optional.of(zonaCocina));
            when(productoTipoRepository.findByZonaTrabajo(zonaCocina)).thenReturn(List.of(tipoComida));

            List<ProductoTipoDTO> resultado = productoService.getTiposKds("COCINA");

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Hamburguesas");
        }

        @Test
        @DisplayName("Debe lanzar NoEncontradoException si la zona de trabajo no existe")
        void getTiposKds_zonaInvalida_lanzaNoEncontradoException() {
            when(zonaTrabajoRepository.findByNombre("BARRA_EXTERIOR")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productoService.getTiposKds("BARRA_EXTERIOR"))
                    .isInstanceOf(NoEncontradoException.class);
        }
    }
}
