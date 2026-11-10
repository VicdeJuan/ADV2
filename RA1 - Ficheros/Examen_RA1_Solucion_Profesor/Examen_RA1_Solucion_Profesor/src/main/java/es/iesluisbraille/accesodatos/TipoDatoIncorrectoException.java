package es.iesluisbraille.accesodatos;

/** Excepción del parseador: un campo numérico no contiene un número válido. */
public class TipoDatoIncorrectoException extends Exception {
    public TipoDatoIncorrectoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
