package es.iesluisbraille.accesodatos.jardin.bloque03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Provoca un error de sintaxis SQL y muestra la información de la excepción. */
public final class ErrorSql {
    private ErrorSql() {
    }

    public static void main(String[] args) {
        // Cada getConnection("...:memory:") crearía una base en memoria diferente.
        // Todas las sentencias usan esta misma Connection para compartir la tabla.
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
            sentencia.execute("CREATE TABLE arbol_demo (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL)");

            try {
                // executeUpdate se usa para DML y devuelve las filas afectadas.
                // Aquí el error de escritura INSRT hace que el driver lance SQLException.
                sentencia.executeUpdate("INSRT INTO arbol_demo (id, nombre) VALUES (1, 'Roble')");
            } catch (SQLException e) {
                System.out.println("Error SQL esperado: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
            }

            // Con auto-commit activo, el INSERT correcto se confirma al terminar la llamada.
            int insertadas = sentencia.executeUpdate(
                    "INSERT INTO arbol_demo (id, nombre) VALUES (1, 'Roble')");
            System.out.println("Filas insertadas tras corregir la sentencia: " + insertadas);
        } catch (SQLException e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}
