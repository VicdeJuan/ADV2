package es.iesluisbraille.accesodatos.jardin.bloque03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Ejecuta varias instrucciones fijas y observa las filas afectadas. */
public final class VariasSentencias {
    private VariasSentencias() {
    }

    public static void main(String[] args) {
        // La base efímera evita modificar el fichero del jardín durante la demostración.
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");

            // JDBC permite executeUpdate para DDL; en SQLite CREATE TABLE informa 0 filas.
            int filasDdl = sentencia.executeUpdate("""
                    CREATE TABLE botanico_demo (
                        id INTEGER PRIMARY KEY,
                        nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0)
                    ) STRICT
                    """);
            System.out.println("Filas afectadas por CREATE TABLE: " + filasDdl);

            // En INSERT/UPDATE/DELETE, el entero permite comprobar el efecto real del SQL.
            int altaUno = sentencia.executeUpdate(
                    "INSERT INTO botanico_demo (id, nombre) VALUES (1, 'Ana')");
            int altaDos = sentencia.executeUpdate(
                    "INSERT INTO botanico_demo (id, nombre) VALUES (2, 'Luis')");
            int cambio = sentencia.executeUpdate(
                    "UPDATE botanico_demo SET nombre = 'Luis Miguel' WHERE id = 2");
            int borrado = sentencia.executeUpdate(
                    "DELETE FROM botanico_demo WHERE id = 1");
            int sinCambio = sentencia.executeUpdate(
                    "UPDATE botanico_demo SET nombre = 'Nadie' WHERE id = 999");

            System.out.println("Filas insertadas: " + altaUno + " y " + altaDos);
            System.out.println("Filas actualizadas: " + cambio);
            System.out.println("Filas eliminadas: " + borrado);
            System.out.println("Actualización sin coincidencia: " + sinCambio);
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
        }
    }
}
