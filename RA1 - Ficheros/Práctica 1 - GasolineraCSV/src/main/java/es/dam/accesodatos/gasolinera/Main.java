package es.dam.accesodatos.gasolinera;

import java.nio.file.Path;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        // La primera práctica conserva el almacenamiento CSV.
        Almacenamiento almacenamiento = new AlmacenamientoCsv(Path.of("data"));
        try {
            almacenamiento.preparar();
            new AplicacionConsola(new ServicioGasolinera(almacenamiento)).ejecutar();
        } catch (ErrorAlmacenamiento e) {
            System.out.println("No se puede iniciar la aplicación: " + e.getMessage());
        }
    }
}
