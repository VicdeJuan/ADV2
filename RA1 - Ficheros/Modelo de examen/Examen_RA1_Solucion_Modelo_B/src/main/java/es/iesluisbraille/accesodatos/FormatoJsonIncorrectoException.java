package es.iesluisbraille.accesodatos;

/** Sintaxis JSON incorrecta o propiedades que no corresponden al esquema. */
public final class FormatoJsonIncorrectoException extends Exception {
    public FormatoJsonIncorrectoException(String mensaje) {
        super(mensaje);
    }
}
