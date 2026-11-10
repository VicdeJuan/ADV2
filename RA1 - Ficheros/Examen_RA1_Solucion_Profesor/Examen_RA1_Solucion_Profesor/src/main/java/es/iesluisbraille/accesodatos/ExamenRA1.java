package es.iesluisbraille.accesodatos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/** SOLUCIÓN DE REFERENCIA PARA EL PROFESORADO; no entregar al alumnado. */
public class ExamenRA1 {
    private static final Path DIRECTORIO_ENTRADA = Path.of("datos", "entrada");
    private static final Path FICHERO_1 = DIRECTORIO_ENTRADA.resolve("alumnos1.csv");
    private static final Path FICHERO_2 = DIRECTORIO_ENTRADA.resolve("alumnos2.csv");
    private static final Path FICHERO_3 = DIRECTORIO_ENTRADA.resolve("alumnos3.csv");

    public static void main(String[] args) {
        System.out.println("=== EXAMEN RA1: GESTIÓN DE FICHEROS ===");
        System.out.println("Directorio de entrada: " + DIRECTORIO_ENTRADA.toAbsolutePath());

        Path directorioSalida;
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Introduce la ruta del directorio de salida: ");
            String ruta = teclado.nextLine().trim();
            if (ruta.isEmpty()) {
                System.err.println("La ruta de salida no puede estar vacía.");
                return;
            }
            directorioSalida = Path.of(ruta);
        }

        if (!prepararDirectorioSalida(directorioSalida)) {
            System.err.println("No se puede continuar sin un directorio de salida válido.");
            return;
        }

        System.out.println("[Flujo] Ejercicio 1 finalizado; comienza el procesamiento.");
        System.out.println("[Flujo] Ejercicio 2");
        Alumno alumno1 = ejercicio2LeerYEscribirAlumno(FICHERO_1, directorioSalida);
        System.out.println("[Flujo] Ejercicio 3");
        List<Alumno> alumnos2 = ejercicio3LeerAlumnos(FICHERO_2);
        System.out.println("[Flujo] Ejercicio 4");
        ejercicio4EscribirNumeroBinario(directorioSalida, alumnos2);
        System.out.println("[Flujo] Ejercicio 5");
        List<Alumno> alumnos3 = ejercicio5LeerAlumnosConErrores(FICHERO_3);
        System.out.println("[Flujo] Ejercicio 6");
        ejercicio6FusionarYGenerar(alumnos2, alumnos3, alumno1, directorioSalida);
        System.out.println("[Flujo] Fin de la ejecución.");
    }

    private static boolean prepararDirectorioSalida(Path directorio) {
        try {
            if (Files.exists(directorio)) {
                if (Files.isDirectory(directorio)) {
                    System.out.println("[Ej. 1] El directorio ya existía: " + directorio);
                    return true;
                }
                System.err.println("[Ej. 1] La ruta existe, pero no es un directorio: " + directorio);
                return false;
            }
            Files.createDirectories(directorio);
            System.out.println("[Ej. 1] Directorio creado: " + directorio);
            return true;
        } catch (IOException | SecurityException ex) {
            System.err.println("[Ej. 1] No se pudo crear el directorio: " + ex.getMessage());
            return false;
        }
    }

    private static Alumno ejercicio2LeerYEscribirAlumno(Path entrada, Path salida) {
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea = lector.readLine();
            Alumno alumno = ParseadorAlumnos.parsear(linea);
            if (alumno == null) {
                System.err.println("[Ej. 2] La línea del fichero 1 no es válida.");
                return null;
            }
            Path destino = salida.resolve("alumno1.txt");
            try (BufferedWriter escritor = Files.newBufferedWriter(destino,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                escritor.write(alumno.getNombre());
                escritor.newLine();
            }
            System.out.println("[Ej. 2] Nombre escrito en " + destino);
            return alumno;
        } catch (IOException | NumeroCamposIncorrectoException | TipoDatoIncorrectoException ex) {
            System.err.println("[Ej. 2] No se pudo procesar el fichero: " + ex.getMessage());
            return null;
        }
    }

    private static List<Alumno> ejercicio3LeerAlumnos(Path entrada) {
        List<Alumno> alumnos = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                try {
                    Alumno alumno = ParseadorAlumnos.parsear(linea);
                    if (alumno != null) {
                        alumnos.add(alumno);
                    } else {
                        System.err.println("[Ej. 3] Línea " + numeroLinea + " descartada (null).");
                    }
                } catch (NumeroCamposIncorrectoException | TipoDatoIncorrectoException ex) {
                    System.err.println("[Ej. 3] Línea " + numeroLinea + ": " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("[Ej. 3] Error de lectura: " + ex.getMessage());
        }
        System.out.println("[Ej. 3] Alumnos incorporados: " + alumnos.size());
        return alumnos;
    }

    private static void ejercicio4EscribirNumeroBinario(Path salida, List<Alumno> alumnos2) {
        Path destino = salida.resolve("numero_alumnos.bin");
        try (DataOutputStream datos = new DataOutputStream(
                Files.newOutputStream(destino,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE))) {
            datos.writeInt(alumnos2.size());
            System.out.println("[Ej. 4] Entero " + alumnos2.size() + " escrito en " + destino);
        } catch (IOException ex) {
            System.err.println("[Ej. 4] Error de escritura binaria: " + ex.getMessage());
        }
    }

    private static List<Alumno> ejercicio5LeerAlumnosConErrores(Path entrada) {
        List<Alumno> alumnos = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                try {
                    Alumno alumno = ParseadorAlumnos.parsear(linea);
                    if (alumno == null) {
                        System.err.println("[Ej. 5] Línea " + numeroLinea
                                + " descartada: el parseador devolvió null.");
                    } else if (!ids.add(alumno.getId())) {
                        System.err.println("[Ej. 5] Línea " + numeroLinea
                                + " descartada: ID repetido " + alumno.getId());
                    } else {
                        alumnos.add(alumno);
                    }
                } catch (NumeroCamposIncorrectoException ex) {
                    System.err.println("[Ej. 5] Línea " + numeroLinea
                            + " descartada (número de campos): " + ex.getMessage());
                } catch (TipoDatoIncorrectoException ex) {
                    System.err.println("[Ej. 5] Línea " + numeroLinea
                            + " descartada (tipo de dato): " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("[Ej. 5] Error leyendo el fichero: " + ex.getMessage());
        }
        System.out.println("[Ej. 5] Alumnos válidos no repetidos: " + alumnos.size());
        return alumnos;
    }

    private static void ejercicio6FusionarYGenerar(
            List<Alumno> alumnos2, List<Alumno> alumnos3, Alumno alumno1, Path salida) {
        List<Alumno> fusionados = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        for (Alumno alumno : alumnos2) {
            if (ids.add(alumno.getId())) {
                fusionados.add(alumno);
            }
        }
        for (Alumno alumno : alumnos3) {
            if (ids.add(alumno.getId())) {
                fusionados.add(alumno);
            }
        }
        Collections.sort(fusionados);
        Path destino = salida.resolve("alumnos_ordenados.csv");

        // Si ya existe el archivo, CREATE_NEW lanza FileAlreadyExistsException.
        // En ese caso no se modifica nada ni se realiza la parte B.
        try (BufferedWriter escritor = Files.newBufferedWriter(destino,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE)) {
            for (Alumno alumno : fusionados) {
                escritor.write(alumno.toCsv());
                escritor.newLine();
            }
        } catch (FileAlreadyExistsException ex) {
            System.out.println("[Ej. 6] El fichero ya existe; no se ha modificado: " + destino);
            return;
        } catch (IOException ex) {
            System.err.println("[Ej. 6] Falló la primera escritura: " + ex.getMessage());
            return;
        }
        System.out.println("[Ej. 6] Parte A completada: " + fusionados.size() + " alumnos.");

        if (alumno1 != null && ids.add(alumno1.getId())) {
            fusionados.add(alumno1);
        }
        Collections.sort(fusionados);
        try (BufferedWriter escritor = Files.newBufferedWriter(destino,
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (Alumno alumno : fusionados) {
                escritor.write(alumno.toCsv());
                escritor.newLine();
            }
            System.out.println("[Ej. 6] Parte B completada: " + fusionados.size() + " alumnos.");
        } catch (IOException ex) {
            System.err.println("[Ej. 6] Falló la segunda escritura: " + ex.getMessage());
        }
    }
}
