package es.iesluisbraille.accesodatos.jardin.bloque02;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Crea la base de datos SQLite "botanico.sqlite" a partir de:
 *   - sql/sqlite/01-esquema.sql  (tablas e índices)
 *   - sql/sqlite/02-datos.sql    (INSERTs)
 *
 *   OJO: Solo si no puedes ejecutar en consola queries SQLite.
 */
public final class CrearBaseDatos {
    private static final String[] TABLAS = {"botanico", "credencial", "parcela", "arbol", "estudio"};

    private CrearBaseDatos() {
    }

    public static void main(String[] args) {
        Path archivo = Path.of("botanicov2.sqlite");
        Path esquema = Path.of("sql","sqlite", "01-esquema.sql");
        Path datos = Path.of("sql","sqlite", "02-datos.sql");

        try {
            Files.deleteIfExists(archivo); // empezar siempre desde una base nueva

            try (Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + archivo.toAbsolutePath())) {
                // Las FK hay que activarlas en CADA conexión (y antes de abrir la transacción)
                try (Statement configuracion = conexion.createStatement()) {
                    configuracion.execute("PRAGMA foreign_keys = ON");
                }

                conexion.setAutoCommit(false);
                try {
                    int n1 = ejecutarScript(conexion, esquema);
                    int n2 = ejecutarScript(conexion, datos);
                    conexion.commit();
                    System.out.println("Sentencias ejecutadas -> esquema: " + n1 + ", datos: " + n2);
                } catch (SQLException | IOException e) {
                    conexion.rollback();
                    throw e;
                }
                verificar(conexion);
            }
            System.out.println("Base de datos creada: " + archivo.toAbsolutePath());
        } catch (SQLException | IOException e) {
            System.err.println("Error al crear la base SQLite: " + e.getMessage());
        }
    }

    /** Ejecuta las sentencias de un fichero .sql dentro de la transacción actual. */
    private static int ejecutarScript(Connection conexion, Path fichero) throws IOException, SQLException {
        String sql = Files.readString(fichero, StandardCharsets.UTF_8);
        int total = 0;
        try (Statement st = conexion.createStatement()) {
            for (String sentencia : dividirSentencias(sql)) {
                String s = sentencia.toUpperCase();
                // PRAGMA/BEGIN/COMMIT los gestiona Java (conexión y transacción)
                if (s.startsWith("PRAGMA") || s.startsWith("BEGIN") || s.startsWith("COMMIT")) {
                    continue;
                }
                st.execute(sentencia);
                total++;
            }
        }
        return total;
    }

    /** Divide el script por ';' ignorando los que van entre comillas simples y los comentarios "--". */
    static List<String> dividirSentencias(String sql) {
        List<String> resultado = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enComillas = false;
        int n = sql.length();
        for (int i = 0; i < n; i++) {
            char c = sql.charAt(i);
            if (enComillas) {
                actual.append(c);
                if (c == '\'') {
                    enComillas = false; // '' (comilla escapada) reabre en la siguiente vuelta
                }
            } else if (c == '\'') {
                enComillas = true;
                actual.append(c);
            } else if (c == '-' && i + 1 < n && sql.charAt(i + 1) == '-') {
                while (i < n && sql.charAt(i) != '\n') {
                    i++; // saltar comentario de línea
                }
                actual.append('\n');
            } else if (c == ';') {
                anadir(resultado, actual);
            } else {
                actual.append(c);
            }
        }
        anadir(resultado, actual);
        return resultado;
    }

    private static void anadir(List<String> resultado, StringBuilder actual) {
        String s = actual.toString().trim();
        if (!s.isEmpty()) {
            resultado.add(s);
        }
        actual.setLength(0);
    }

    /** Comprueba nº de filas por tabla, claves foráneas e integridad. */
    private static void verificar(Connection conexion) throws SQLException {
        try (Statement st = conexion.createStatement()) {
            for (String tabla : TABLAS) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
                    rs.next();
                    System.out.printf("  %-11s %6d filas%n", tabla, rs.getInt(1));
                }
            }
            try (ResultSet rs = st.executeQuery("PRAGMA foreign_key_check")) {
                System.out.println("  foreign_key_check: " + (rs.next() ? "HAY VIOLACIONES" : "OK"));
            }
            try (ResultSet rs = st.executeQuery("PRAGMA integrity_check")) {
                rs.next();
                System.out.println("  integrity_check:   " + rs.getString(1));
            }
        }
    }
}