package es.dam.accesodatos.gasolinera;

/** Error de lectura, interpretación o escritura de los ficheros de datos. */
public class ErrorAlmacenamiento extends Exception {
    public ErrorAlmacenamiento(String mensaje) {
        super(mensaje);
    }

    public ErrorAlmacenamiento(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
