package es.iesluisbraille.accesodatos.jardin.bloque03;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Crea una tabla de demostración y comprueba que existe. */
public final class CrearTabla {
    private CrearTabla() {
    }

    public static void main(String[] args) {
        // :memory: es el nombre especial de SQLite para una base temporal en RAM.
        // Vive exactamente lo que esta conexión: al cerrarla se pierden tabla y datos.
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");

            String ddl = """
                    CREATE TABLE parcela_demo (
                        id INTEGER PRIMARY KEY CHECK (id > 0),
                        nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0)
                    ) STRICT
                    """;
            // execute acepta cualquier tipo de SQL. Su booleano indica si el primer
            // resultado es un ResultSet; CREATE TABLE no produce filas y devuelve false.
            boolean devuelveFilas = sentencia.execute(ddl);
            System.out.println("CREATE TABLE devuelve filas: " + devuelveFilas);

            // DatabaseMetaData permite inspeccionar el catálogo sin consultar las tablas de negocio.
            DatabaseMetaData metadatos = conexion.getMetaData();
            // null significa "sin restringir" catálogo y esquema; el tercer argumento
            // es el patrón del nombre y TABLE limita el tipo de objeto buscado.
            try (ResultSet tablas = metadatos.getTables(null, null, "parcela_demo", new String[]{"TABLE"})) {
                // El cursor empieza antes de la primera fila; next() lo avanza y dice si existe.
                System.out.println("La tabla existe: " + tablas.next());
            }

            sentencia.execute("CREATE TABLE IF NOT EXISTS parcela_demo (id INTEGER PRIMARY KEY)");
            System.out.println("CREATE TABLE IF NOT EXISTS no produjo error.");
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
        }
    }
}
