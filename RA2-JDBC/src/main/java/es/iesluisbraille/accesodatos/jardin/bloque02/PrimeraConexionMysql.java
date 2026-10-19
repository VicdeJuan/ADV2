package es.iesluisbraille.accesodatos.jardin.bloque02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Conexión a un servidor MySQL de prácticas, sin credenciales en el código. */
public final class PrimeraConexionMysql {
    private PrimeraConexionMysql() {
    }

    public static void main(String[] args) {
        String url = System.getenv("JARDIN_MYSQL_URL");
        String usuario = System.getenv("JARDIN_MYSQL_USER");
        String clave = System.getenv("JARDIN_MYSQL_PASSWORD");
        if (url == null || url.isBlank() || usuario == null || usuario.isBlank()) {
            System.err.println("Configura JARDIN_MYSQL_URL y JARDIN_MYSQL_USER.");
            return;
        }
        if (clave == null) {
            clave = "";
        }

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
            DatabaseMetaData datos = conexion.getMetaData();
            System.out.println("Gestor: " + datos.getDatabaseProductName());
            System.out.println("Versión del gestor: " + datos.getDatabaseProductVersion());
            System.out.println("Driver: " + datos.getDriverName());
            System.out.println("Conexión válida: " + conexion.isValid(2));
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }
}
