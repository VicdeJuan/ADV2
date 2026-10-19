package es.iesluisbraille.accesodatos.jardin.bloque02;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConexionSqliteTest {
    @TempDir
    Path temporal;

    @Test
    void unaConexionNuevaPermiteActivarYAplicarClavesForaneas() throws SQLException {
        String url = "jdbc:sqlite:" + temporal.resolve("prueba.db");
        Connection conexion;
        try (Connection abierta = DriverManager.getConnection(url);
             Statement sentencia = abierta.createStatement()) {
            conexion = abierta;
            sentencia.execute("PRAGMA foreign_keys = ON");
            try (ResultSet resultado = sentencia.executeQuery("PRAGMA foreign_keys")) {
                assertTrue(resultado.next());
                assertEquals(1, resultado.getInt(1));
            }
            sentencia.execute("CREATE TABLE padre (id INTEGER PRIMARY KEY)");
            sentencia.execute("CREATE TABLE hijo (padre_id INTEGER NOT NULL REFERENCES padre(id))");
            assertThrows(SQLException.class,
                    () -> sentencia.executeUpdate("INSERT INTO hijo(padre_id) VALUES (999)"));
        }
        assertTrue(conexion.isClosed());
    }
}
