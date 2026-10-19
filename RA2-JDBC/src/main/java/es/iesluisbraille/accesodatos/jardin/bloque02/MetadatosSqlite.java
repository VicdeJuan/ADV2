package es.iesluisbraille.accesodatos.jardin.bloque02;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Explora la información que ofrece una conexión JDBC. */
public final class MetadatosSqlite {
    private MetadatosSqlite() {
    }

    public static void main(String[] args) {
        Path archivo = args.length == 0 ? Path.of("data", "jardin-botanico.db") : Path.of(args[0]);
        if (!Files.isRegularFile(archivo)) {
            System.err.println("No existe la base SQLite: " + archivo.toAbsolutePath());
            return;
        }

        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + archivo.toAbsolutePath())) {
            try (Statement configuracion = conexion.createStatement()) {
                configuracion.execute("PRAGMA foreign_keys = ON");
            }
            DatabaseMetaData datos = conexion.getMetaData();
            System.out.println("Gestor: " + datos.getDatabaseProductName());
            System.out.println("Versión del gestor: " + datos.getDatabaseProductVersion());
            System.out.println("Driver: " + datos.getDriverName());
            System.out.println("Versión del driver: " + datos.getDriverVersion());
            System.out.println("Conexión válida: " + conexion.isValid(2));
            System.out.println("Cerrada dentro del bloque: " + conexion.isClosed());
        } catch (SQLException e) {
            System.err.println("Error al consultar metadatos de SQLite: " + e.getMessage());
        }
    }
}
