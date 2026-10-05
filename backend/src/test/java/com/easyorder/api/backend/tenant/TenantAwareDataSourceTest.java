package com.easyorder.api.backend.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantAwareDataSourceTest {

    @Mock
    private DataSource targetDataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    private TenantAwareDataSource tenantAwareDataSource;

    @BeforeEach
    void setUp() throws SQLException {
        tenantAwareDataSource = new TenantAwareDataSource(targetDataSource);
        when(targetDataSource.getConnection()).thenReturn(connection);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Cuando no hay tenant en contexto, no altera la conexión ni ejecuta queries")
    void getConnection_sinTenant_noModificaConexion() throws SQLException {
        Connection result = tenantAwareDataSource.getConnection();

        assertThat(result).isSameAs(connection);
        verify(connection, never()).setAutoCommit(false);
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    @DisplayName("Cuando hay tenant en contexto, fija app.current_tenant_id con set_config")
    void getConnection_conTenant_fijaVariableEnPostgres() throws SQLException {
        TenantContext.set(123L);
        when(connection.prepareStatement("SELECT set_config(?, ?, true)")).thenReturn(preparedStatement);

        Connection result = tenantAwareDataSource.getConnection();

        assertThat(result).isSameAs(connection);
        verify(connection).setAutoCommit(false);
        verify(preparedStatement).setString(1, "app.current_tenant_id");
        verify(preparedStatement).setString(2, "123");
        verify(preparedStatement).execute();
    }

    @Test
    @DisplayName("Si ocurre un error SQL fijando el tenant, cierra la conexión y relanza la excepción")
    void getConnection_errorSql_cierraConexionYPropaga() throws SQLException {
        TenantContext.set(999L);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Error de conexión"));

        assertThatThrownBy(() -> tenantAwareDataSource.getConnection())
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Error de conexión");

        verify(connection).close();
    }
}
