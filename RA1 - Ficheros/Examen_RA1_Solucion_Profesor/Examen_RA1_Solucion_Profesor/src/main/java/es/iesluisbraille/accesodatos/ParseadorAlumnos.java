package es.iesluisbraille.accesodatos;

/** Funcionalidad proporcionada. No hay que implementarla ni modificarla. */
public final class ParseadorAlumnos {
    private ParseadorAlumnos() {
    }

    /**
     * Convierte una línea de texto en un Alumno.
     *
     * Formato: id;nombre;apellidos;edad;notaMedia
     * - Si no hay exactamente cinco campos: NumeroCamposIncorrectoException.
     * - Si id, edad o notaMedia no son numéricos: TipoDatoIncorrectoException.
     * - Si hay datos vacíos o valores fuera de rango: null.
     * - En caso válido: Alumno.
     *
     * El separador es ';' y el decimal es '.' (por ejemplo, 7.50).
     * No se requiere gestionar comillas o campos con separadores internos.
     */
    public static Alumno parsear(String linea)
            throws NumeroCamposIncorrectoException, TipoDatoIncorrectoException {
        if (linea == null) {
            return null;
        }

        String[] campos = linea.split(";", -1);
        if (campos.length != 5) {
            throw new NumeroCamposIncorrectoException(
                    "Se esperaban 5 campos separados por ';', pero se encontraron " + campos.length);
        }

        String nombre = campos[1].trim();
        String apellidos = campos[2].trim();
        int id;
        int edad;
        double notaMedia;
        try {
            id = Integer.parseInt(campos[0].trim());
            edad = Integer.parseInt(campos[3].trim());
            notaMedia = Double.parseDouble(campos[4].trim());
        } catch (NumberFormatException e) {
            throw new TipoDatoIncorrectoException(
                    "El identificador, la edad o la nota media contienen un tipo de dato incorrecto", e);
        }

        if (id <= 0 || nombre.isEmpty() || apellidos.isEmpty()
                || edad < 16 || edad > 100
                || !Double.isFinite(notaMedia) || notaMedia < 0 || notaMedia > 10) {
            return null;
        }

        return new Alumno(id, nombre, apellidos, edad, notaMedia);
    }
}
