package es.iesluisbraille.accesodatos;

/** Excepción del parseador: una línea no tiene exactamente cinco columnas CSV. */
public class NumeroCamposIncorrectoException extends Exception {
    public NumeroCamposIncorrectoException(String mensaje) {
        super(mensaje);
    }
}
