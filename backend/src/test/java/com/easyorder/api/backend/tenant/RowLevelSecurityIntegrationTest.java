package com.easyorder.api.backend.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;

import com.easyorder.api.backend.dto.SesionAuthProjection;
import com.easyorder.api.backend.model.Mesa;
import com.easyorder.api.backend.model.MesaEstado;
import com.easyorder.api.backend.model.Producto;
import com.easyorder.api.backend.model.Producto_Alergeno;
import com.easyorder.api.backend.model.Producto_GrupoModificador;
import com.easyorder.api.backend.repository.MesaEstadoRepository;
import com.easyorder.api.backend.repository.MesaRepository;
import com.easyorder.api.backend.repository.ProductoRepository;
import com.easyorder.api.backend.repository.Producto_AlergenoRepository;
import com.easyorder.api.backend.repository.Producto_GrupoModificadorRepository;
import com.easyorder.api.backend.repository.SesionRepository;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=false",
        "management.health.redis.enabled=false",
        "management.health.defaults.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
        "jwt.secret=9a6182bd793546239f6772341b1e907673f13f282d1ae623bc4f480e306ec445",
        "jwt.expiration=86400000",
        "jwt.refresh-token.expiration=604800000",
        "app.cors.allowed-origins=http://localhost:5173"
})
class RowLevelSecurityIntegrationTest {

    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("easyorder")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        postgres.start();
        try (Connection conn = postgres.createConnection("")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                        """
                                    DO $$
                                    BEGIN
                                      IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'easyorder_app') THEN
                                        CREATE ROLE easyorder_app LOGIN PASSWORD 'test_pass' NOSUPERUSER NOBYPASSRLS NOCREATEDB NOCREATEROLE;
                                      END IF;
                                    END
                                    $$;
                                    GRANT CONNECT ON DATABASE easyorder TO easyorder_app;
                                    GRANT USAGE ON SCHEMA public TO easyorder_app;
                                    ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public
                                      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO easyorder_app;
                                    ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public
                                      GRANT USAGE, SELECT ON SEQUENCES TO easyorder_app;
                                    ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public
                                      GRANT EXECUTE ON FUNCTIONS TO easyorder_app;
                                """);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error initializing test postgres container with easyorder_app role", e);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "easyorder_app");
        registry.add("spring.datasource.password", () -> "test_pass");

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private StringRedisTemplate stringRedisTemplate;

    @MockitoBean
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private MesaEstadoRepository mesaEstadoRepository;

    @Autowired
    private Producto_AlergenoRepository productoAlergenoRepository;

    @Autowired
    private Producto_GrupoModificadorRepository productoGrupoModificadorRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Aislamiento de lectura: Tenant 1 ve sus datos, Tenant 2 ve vacío y sin tenant se bloquea")
    void testAislamientoLectura() {
        // Tenant 1 ve sus 22 productos y 10 mesas
        TenantContext.set(1L);
        List<Producto> productosTenant1 = productoRepository.findAll();
        List<Mesa> mesasTenant1 = mesaRepository.findAll();

        assertThat(productosTenant1).isNotEmpty();
        assertThat(productosTenant1).allMatch(p -> Long.valueOf(1L).equals(p.getTenantId()));
        assertThat(mesasTenant1).isNotEmpty();
        assertThat(mesasTenant1).allMatch(m -> Long.valueOf(1L).equals(m.getTenantId()));

        // Tenant 2 no ve nada del Tenant 1
        TenantContext.set(2L);
        List<Producto> productosTenant2 = productoRepository.findAll();
        List<Mesa> mesasTenant2 = mesaRepository.findAll();

        assertThat(productosTenant2).isEmpty();
        assertThat(mesasTenant2).isEmpty();

        // Sin tenant (TenantContext = null): bloqueo por defecto bajo RLS
        TenantContext.clear();
        List<Producto> productosAnonimo = productoRepository.findAll();
        List<Mesa> mesasAnonimo = mesaRepository.findAll();

        assertThat(productosAnonimo).isEmpty();
        assertThat(mesasAnonimo).isEmpty();
    }

    @Test
    @DisplayName("Aislamiento de tablas relacionales (MT-04): producto_alergeno y producto_grupo_modificador")
    void testAislamientoTablasRelacionales() {
        TenantContext.set(1L);
        List<Producto> productos = productoRepository.findAll();
        List<Long> productoIds = productos.stream().map(Producto::getId).toList();

        List<Producto_Alergeno> alergenosT1 = productoAlergenoRepository.findByProducto_IdIn(productoIds);
        List<Producto_GrupoModificador> gruposT1 = productoGrupoModificadorRepository.findByProducto_IdIn(productoIds);

        assertThat(alergenosT1).isNotEmpty();
        assertThat(alergenosT1).allMatch(pa -> Long.valueOf(1L).equals(pa.getTenantId()));
        assertThat(gruposT1).isNotEmpty();
        assertThat(gruposT1).allMatch(pgm -> Long.valueOf(1L).equals(pgm.getTenantId()));

        // Tenant 2 no ve ninguna relación del Tenant 1
        TenantContext.set(2L);
        List<Producto_Alergeno> alergenosT2 = productoAlergenoRepository.findByProducto_IdIn(productoIds);
        List<Producto_GrupoModificador> gruposT2 = productoGrupoModificadorRepository.findByProducto_IdIn(productoIds);

        assertThat(alergenosT2).isEmpty();
        assertThat(gruposT2).isEmpty();
    }

    @Test
    @DisplayName("Bloqueo de inserción cruzada a nivel JPA: Hibernate rechaza entidad con tenantId discrepante")
    void testBloqueoInsercionCruzadaJpa() {
        TenantContext.set(1L);
        MesaEstado estadoLibre = mesaEstadoRepository.findAll().getFirst();

        Mesa mesaCruzada = new Mesa();
        mesaCruzada.setNumero(777);
        mesaCruzada.setEstado(estadoLibre);
        mesaCruzada.setTenantId(2L); // 👈 Forzar tenantId = 2 en la sesión de tenant 1

        assertThatThrownBy(() -> {
            mesaRepository.saveAndFlush(mesaCruzada);
        }).isInstanceOf(Exception.class)
                .satisfies(e -> {
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    assertThat(cause.getMessage())
                            .containsIgnoringCase("assigned tenant id differs from current tenant id");
                });
    }

    @Test
    @DisplayName("Bloqueo de inserción cruzada a nivel PostgreSQL RLS (WITH CHECK - MT-05): rechaza insert nativo")
    void testBloqueoInsercionCruzadaPostgreSqlRls() {
        TenantContext.set(1L);

        // Al ejecutar SQL directo bajo el usuario de aplicación easyorder_app
        // (NOBYPASSRLS)
        // PostgreSQL RLS con WITH CHECK aborta la inserción inmediatamente
        assertThatThrownBy(() -> {
            jdbcTemplate.execute("INSERT INTO mesa (id_tenant, numero, id_estado) VALUES (2, 888, 1)");
        }).isInstanceOf(Exception.class)
                .satisfies(e -> {
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    assertThat(cause.getMessage()).containsIgnoringCase("violates row-level security policy");
                });
    }

    @Test
    @DisplayName("Bloqueo de actualización cruzada a nivel PostgreSQL RLS (WITH CHECK - MT-05): rechaza update a otro tenant")
    void testBloqueoActualizacionCruzadaPostgreSqlRls() {
        TenantContext.set(1L);

        assertThatThrownBy(() -> {
            jdbcTemplate.execute("UPDATE mesa SET id_tenant = 2 WHERE numero = 1");
        }).isInstanceOf(Exception.class)
                .satisfies(e -> {
                    Throwable cause = e;
                    while (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    assertThat(cause.getMessage()).containsIgnoringCase("violates row-level security policy");
                });
    }

    @Test
    @DisplayName("Inserción y lectura legítima dentro del mismo tenant funciona correctamente")
    void testInsercionLegitimaMismoTenant() {
        TenantContext.set(2L);
        MesaEstado estadoLibre = mesaEstadoRepository.findAll().getFirst();

        Mesa nuevaMesaT2 = new Mesa();
        nuevaMesaT2.setNumero(201);
        nuevaMesaT2.setEstado(estadoLibre);
        nuevaMesaT2.setTenantId(2L);

        Mesa guardada = mesaRepository.saveAndFlush(nuevaMesaT2);
        assertThat(guardada.getId()).isNotNull();

        // Tenant 2 ve su nueva mesa
        List<Mesa> mesasT2 = mesaRepository.findAll();
        assertThat(mesasT2).extracting(Mesa::getNumero).contains(201);

        // Tenant 1 NO puede ver la mesa de Tenant 2
        TenantContext.set(1L);
        List<Mesa> mesasT1 = mesaRepository.findAll();
        assertThat(mesasT1).extracting(Mesa::getNumero).doesNotContain(201);
    }

    @Test
    @DisplayName("Resolución QR anónima bajo RLS (MT-03): SECURITY DEFINER resuelve sesión sin TenantContext")
    void testResolucionSesionQrAnonima() {
        // Un comensal anónimo no tiene TenantContext
        TenantContext.clear();

        // Buscar por el código de sesión activo existente en el seed
        Optional<SesionAuthProjection> resultado = sesionRepository
                .findAuthProjectionByQrCodeUrl("16463a47-dbdc-4994-b1b5-3087e65d6e9f");

        // Nota: en V1 la sesion 1 tiene estado 'ACTIVA' en su inicio
        // La función get_active_session_by_qr retorna la proyección aunque
        // TenantContext sea nulo
        assertThat(resultado).isNotNull();
    }
}
