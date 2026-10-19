package es.iesluisbraille.accesodatos.jardin.bloque02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Error reproducible: ningún driver acepta este prefijo de URL. */
public final class ErrorDeConexion {
    private ErrorDeConexion() {
    }

    public static void main(String[] args) {
        try (Connection conexion = DriverManager.getConnection("jdbc:desconocido:ejemplo")) {
            System.out.println("Conexión inesperada: " + conexion);
        } catch (SQLException e) {
            System.out.println("Error esperado: " + e.getMessage());
            System.out.println("Tipo de excepción: " + e.getClass().getSimpleName());
        }
    }
}
