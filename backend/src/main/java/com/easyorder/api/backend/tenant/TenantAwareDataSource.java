package com.easyorder.api.backend.tenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Envuelve el DataSource real (HikariCP) para fijar la variable de sesión
 * "app.current_tenant_id" en cada conexión, ANTES de que Hibernate/JdbcTemplate
 * ejecute ninguna query. Esa variable es la que lee current_tenant_id() en
 * Postgres, usada por las policies RLS.
 *
 * Usamos set_config(name, value, is_local=true) con PreparedStatement:
 * 1. Previene inyecciones SQL por construcción mediante bind parameters.
 * 2. is_local=true garantiza que el valor solo vive durante la transacción
 * actual;
 * al hacer COMMIT/ROLLBACK, PostgreSQL descarta automáticamente la variable,
 * devolviendo la conexión limpia al pool de HikariCP.
 * 3. Si no hay tenant en contexto (healthchecks, inicio de la app), NO
 * modificamos
 * autoCommit ni ejecutamos queries para evitar transacciones sucias en
 * HikariCP.
 */
@Slf4j
public class TenantAwareDataSource extends DelegatingDataSource {

  private static final String TENANT_GUC = "app.current_tenant_id";
  private static final String SET_TENANT_QUERY = "SELECT set_config(?, ?, true)";

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
    Long tenantId = TenantContext.getOrNull();
    if (tenantId == null) {
      // Sin tenant en contexto (healthchecks, peticiones no autenticadas, arranque):
      // No alteramos autoCommit ni ejecutamos queries para mantener la conexión
      // limpia en HikariCP.
      return;
    }

    try {
      connection.setAutoCommit(false);
      try (PreparedStatement ps = connection.prepareStatement(SET_TENANT_QUERY)) {
        ps.setString(1, TENANT_GUC);
        ps.setString(2, tenantId.toString());
        ps.execute();
      }
    } catch (SQLException e) {
      log.error("Error crítico fijando tenant {} en la conexión JDBC", tenantId, e);
      connection.close();
      throw e;
    }
  }
}