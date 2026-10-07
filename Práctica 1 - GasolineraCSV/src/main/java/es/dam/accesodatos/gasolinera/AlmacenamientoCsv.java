package es.dam.accesodatos.gasolinera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda clientes y pagos en dos ficheros CSV UTF-8.
 * Esta clase concentra tanto la lectura y escritura con búfer como el formato CSV: para esta
 * práctica no es necesaria una clase adicional solo para CSV.
 */
public final class AlmacenamientoCsv implements Almacenamiento {
    private static final String CABECERA_CLIENTES = "id,nombre,telefono,matricula";
    private static final String CABECERA_PAGOS = "id,clienteId,fecha,importe,litros,combustible";
    private final Path clientes;
    private final Path pagos;

    public AlmacenamientoCsv(Path directorio) {
        clientes = directorio.resolve("clientes.csv");
        pagos = directorio.resolve("pagos.csv");
    }

    /** En el primer arranque crea el directorio y los dos CSV con su cabecera. */
    @Override
    public void preparar() throws ErrorAlmacenamiento {
        try {
            Files.createDirectories(clientes.getParent());
            if (Files.notExists(clientes)) {
                try (BufferedWriter escritor = Files.newBufferedWriter(clientes, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW)) {
                    escritor.write(CABECERA_CLIENTES);
                    escritor.newLine();
                }
            }
            if (Files.notExists(pagos)) {
                try (BufferedWriter escritor = Files.newBufferedWriter(pagos, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW)) {
                    escritor.write(CABECERA_PAGOS);
                    escritor.newLine();
                }
            }
        } catch (IOException e) {
            throw new ErrorAlmacenamiento("No se ha podido preparar el directorio de datos.", e);
        }
    }

    @Override
    public List<Cliente> cargarClientes() throws ErrorAlmacenamiento {
        List<List<String>> filas = leerFilas(clientes, CABECERA_CLIENTES);
        List<Cliente> resultado = new ArrayList<>();

        for (int indice = 0; indice < filas.size(); indice++) {
            List<String> fila = filas.get(indice);
            if (fila.size() != 4) {
                throw errorEnFila(clientes, indice + 2, "se esperaban 4 columnas");
            }
            try {
                int id = Integer.parseInt(fila.get(0));
                if (id <= 0 || fila.get(1).isBlank() || fila.get(2).isBlank() || fila.get(3).isBlank()) {
                    throw new IllegalArgumentException();
                }
                resultado.add(new Cliente(id, fila.get(1), fila.get(2), fila.get(3)));
            } catch (IllegalArgumentException e) {
                throw errorEnFila(clientes, indice + 2, "datos de cliente no válidos");
            }
        }
        return resultado;
    }

    @Override
    public List<Pago> cargarPagos() throws ErrorAlmacenamiento {
        List<List<String>> filas = leerFilas(pagos, CABECERA_PAGOS);
        List<Pago> resultado = new ArrayList<>();

        for (int indice = 0; indice < filas.size(); indice++) {
            List<String> fila = filas.get(indice);
            if (fila.size() != 6) {
                throw errorEnFila(pagos, indice + 2, "se esperaban 6 columnas");
            }
            try {
                int id = Integer.parseInt(fila.get(0));
                int clienteId = Integer.parseInt(fila.get(1));
                BigDecimal importe = new BigDecimal(fila.get(3));
                BigDecimal litros = new BigDecimal(fila.get(4));
                if (id <= 0 || clienteId <= 0 || importe.signum() <= 0 || litros.signum() <= 0
                        || importe.scale() > 2 || litros.scale() > 2 || fila.get(5).isBlank()) {
                    throw new IllegalArgumentException();
                }
                resultado.add(new Pago(id, clienteId, LocalDate.parse(fila.get(2)), importe, litros, fila.get(5)));
            } catch (DateTimeParseException | IllegalArgumentException e) {
                throw errorEnFila(pagos, indice + 2, "datos de pago no válidos");
            }
        }
        return resultado;
    }

    @Override
    public void guardarCliente(Cliente cliente) throws ErrorAlmacenamiento {
        guardarFila(clientes, List.of(String.valueOf(cliente.id()), cliente.nombre(), cliente.telefono(), cliente.matricula()));
    }

    @Override
    public void guardarPago(Pago pago) throws ErrorAlmacenamiento {
        guardarFila(pagos, List.of(String.valueOf(pago.id()), String.valueOf(pago.clienteId()),
                pago.fecha().toString(), pago.importe().toPlainString(), pago.litros().toPlainString(), pago.combustible()));
    }

    /** Lee la cabecera y devuelve únicamente las filas que contienen datos. */
    private List<List<String>> leerFilas(Path fichero, String cabeceraEsperada) throws ErrorAlmacenamiento {
        try (BufferedReader lector = Files.newBufferedReader(fichero, StandardCharsets.UTF_8)) {
            if (!cabeceraEsperada.equals(lector.readLine())) {
                throw new ErrorAlmacenamiento("El fichero " + fichero + " no tiene la cabecera esperada. No se modificará.");
            }

            List<List<String>> filas = new ArrayList<>();
            String linea;
            while ((linea = lector.readLine()) != null) {
                filas.add(separarFila(linea));
            }
            return filas;
        } catch (IOException e) {
            throw new ErrorAlmacenamiento("No se ha podido leer " + fichero + ".", e);
        } catch (IllegalArgumentException e) {
            throw new ErrorAlmacenamiento("El fichero " + fichero + " contiene CSV malformado. No se modificará.", e);
        }
    }

    /** Escribe antes de confirmar la operación en la consola. */
    private void guardarFila(Path fichero, List<String> campos) throws ErrorAlmacenamiento {
        try (BufferedWriter escritor = Files.newBufferedWriter(fichero, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            escritor.write(convertirEnFilaCsv(campos));
            escritor.newLine();
        } catch (IOException e) {
            throw new ErrorAlmacenamiento("No se ha podido guardar en " + fichero + ".", e);
        }
    }

    /**
     * Divide una línea CSV. Todos los campos escritos por esta aplicación van
     * entre comillas; una comilla interna se representa como dos comillas.
     */
    private List<String> separarFila(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;

        for (int posicion = 0; posicion < linea.length(); posicion++) {
            char caracter = linea.charAt(posicion);
            if (caracter == '"') {
                if (entreComillas && posicion + 1 < linea.length() && linea.charAt(posicion + 1) == '"') {
                    campo.append('"');
                    posicion++; // Se consume la segunda comilla del escape.
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == ',' && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }

        if (entreComillas) {
            throw new IllegalArgumentException("Comilla sin cerrar");
        }
        campos.add(campo.toString());
        return campos;
    }

    /** Convierte los campos a una línea CSV segura frente a comas y comillas. */
    private String convertirEnFilaCsv(List<String> campos) {
        List<String> camposEscapados = new ArrayList<>();
        for (String campo : campos) {
            camposEscapados.add('"' + campo.replace("\"", "\"\"") + '"');
        }
        return String.join(",", camposEscapados);
    }

    private ErrorAlmacenamiento errorEnFila(Path fichero, int numeroFila, String motivo) {
        return new ErrorAlmacenamiento("El fichero " + fichero + " es incorrecto en la fila " + numeroFila
                + " (" + motivo + "). No se modificará.");
    }
}
