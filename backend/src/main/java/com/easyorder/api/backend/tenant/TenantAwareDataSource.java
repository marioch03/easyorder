package com.easyorder.api.backend.tenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Envuelve el DataSource real (HikariCP) para fijar la variable de sesión
 * "app.current_tenant_id" en cada conexión, ANTES de que Hibernate/JdbcTemplate
 * ejecute ninguna query. Esa variable es la que lee current_tenant_id() en
 * Postgres, usada por las 12 policies RLS de
 * V2__add_tenant_id_and_enable_rls.sql.
 *
 * Usamos set_config(name, value, is_local) en vez de "SET LOCAL ..." con
 * concatenación de string: permite bind parameters reales (PreparedStatement),
 * eliminando por construcción cualquier superficie de inyección SQL,
 * independientemente de si TenantContext llega a almacenar en el futuro
 * algo distinto de un Long. is_local=true reproduce exactamente la
 * semántica de "SET LOCAL": el valor solo vive hasta el próximo
 * COMMIT/ROLLBACK de la transacción actual.
 *
 * Orden crítico dentro de applyTenant():
 * 1. autocommit(false) — SIEMPRE antes de set_config(...)/RESET. Si se
 * ejecuta en autocommit=true, Postgres trata la sentencia como su
 * propia transacción implícita: el valor se pierde antes de que se
 * ejecute la siguiente sentencia real.
 * 2. set_config(..., true) o RESET — no hace falta "des-fijarlo" a mano
 * en el caso normal: al terminar la transacción, Postgres descarta
 * solo cualquier valor fijado con is_local=true. El RESET explícito
 * cuando no hay tenant es una garantía extra, no estrictamente
 * necesaria, pero deja el estado inequívoco para current_tenant_id().
 *
 * getConnection() se invoca UNA vez por transacción de Spring (no una vez
 * por query): DataSourceUtils reutiliza la conexión ya vinculada al hilo
 * mientras la transacción @Transactional sigue abierta. El coste extra de
 * este roundtrip es, por tanto, uno por transacción, no uno por query.
 */
@Slf4j
public class TenantAwareDataSource extends DelegatingDataSource {

  private static final String TENANT_GUC = "app.current_tenant_id";
  private static final String SET_TENANT_QUERY = "SELECT set_config(?, ?, true)";
  private static final String RESET_TENANT_QUERY = "RESET " + TENANT_GUC;

  public TenantAwareDataSource(DataSource targetDataSource) {
    super(targetDataSource);
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = super.getConnection();
    applyTenant(connection);
    return connection;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    Connection connection = super.getConnection(username, password);
    applyTenant(connection);
    return connection;
  }

  private void applyTenant(Connection connection) throws SQLException {
    connection.setAutoCommit(false);

    Long tenantId = TenantContext.getOrNull();

    try {
      if (tenantId != null) {
        try (PreparedStatement ps = connection.prepareStatement(SET_TENANT_QUERY)) {
          ps.setString(1, TENANT_GUC);
          ps.setString(2, tenantId.toString());
          ps.execute();
        }
      } else {
        // Sin tenant en contexto (tareas @Scheduled internas, arranque):
        // current_tenant_id() en Postgres devolverá NULL -> RLS deniega
        // todo por defecto (fail closed), nunca fail open.
        try (Statement st = connection.createStatement()) {
          st.execute(RESET_TENANT_QUERY);
        }
      }
    } catch (SQLException e) {
      log.error("Error crítico de seguridad/conexión: no se pudo fijar el tenant {} en JDBC", tenantId, e);
      connection.close();
      throw e;
    }
  }
}