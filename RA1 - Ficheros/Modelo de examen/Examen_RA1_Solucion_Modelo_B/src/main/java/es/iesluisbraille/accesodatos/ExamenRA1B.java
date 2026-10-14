package es.iesluisbraille.accesodatos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Scanner;

public final class ExamenRA1B {
    // Las rutas de entrada son relativas al directorio desde el que se ejecuta el programa.
    private static final Path DIRECTORIO_ENTRADA = Path.of("datos", "entrada");
    private static final Path FICHERO_1 = DIRECTORIO_ENTRADA.resolve("arboles1.jsonl");
    private static final Path FICHERO_2 = DIRECTORIO_ENTRADA.resolve("arboles2.jsonl");
    private static final Path FICHERO_3 = DIRECTORIO_ENTRADA.resolve("arboles3.jsonl");
    private static final Path FICHERO_ID = DIRECTORIO_ENTRADA.resolve("id_arbol.bin");
    public static final String SALIDA_NOMBRE = "arbol1.txt";
    public static final String SALIDA_FINAL = "arboles_ordenados.jsonl";

    private ExamenRA1B() {
    }

    public static void main(String[] args) {
        System.out.println("=== EXAMEN B: ÁRBOLES Y FICHEROS JSON ===");
        System.out.println("Directorio de entrada: " + DIRECTORIO_ENTRADA.toAbsolutePath());

        try (Scanner teclado = new Scanner(System.in)) {
            // El usuario elige dónde se generan los dos ficheros de salida.
            System.out.print("Introduce la ruta del directorio de salida: ");
            String ruta = teclado.nextLine().trim();
            if (ruta.isEmpty()) {
                System.err.println("La ruta de salida no puede estar vacía.");
                return;
            }
            Path salida;
            try {
                // Una cadena no vacía todavía puede contener una ruta inválida para el sistema.
                salida = Path.of(ruta);
            } catch (InvalidPathException e) {
                System.err.println("Ruta de salida incorrecta: " + e.getMessage());
                return;
            }
            if (!prepararDirectorioSalida(salida)) {
                System.err.println("No se puede continuar sin un directorio válido.");
                return;
            }

            // La coordinación ya está resuelta en el proyecto del alumno. Los métodos de lectura
            // informan de sus errores y devuelven null o un mapa vacío para poder continuar.
            Arbol arbol1 = ejercicio2LeerYEscribirArbol(FICHERO_1, salida);
            HashMap<Integer, Arbol> arboles2 = ejercicio3LeerArboles(FICHERO_2);
            ejercicio4LeerIdYMostrarArbol(FICHERO_ID, arboles2);
            HashMap<Integer, Arbol> arboles3 = ejercicio5LeerArbolesConErrores(FICHERO_3);
            ejercicio6FusionarYGenerar(arboles2, arboles3, arbol1, salida);
        }
    }

    /** Ejercicio 1 (1 punto): el directorio existente se reutiliza; el ausente se crea. */
    private static boolean prepararDirectorioSalida(Path salida) {
        try {
            // Si la ruta ya existe, hay que comprobar que sea realmente un directorio.
            if (Files.exists(salida)) {
                if (Files.isDirectory(salida)) {
                    System.out.println("El directorio de salida ya existía: " + salida);
                    return true;
                }
                System.err.println("La ruta de salida existe, pero no es un directorio: " + salida);
                return false;
            }
            // createDirectories crea también los directorios intermedios que falten.
            Files.createDirectories(salida);
            System.out.println("Directorio de salida creado: " + salida);
            return true;
        } catch (IOException | SecurityException e) {
            System.err.println("No se pudo preparar el directorio " + salida + ": " + e.getMessage());
            return false;
        }
    }

    /** Ejercicio 2 (1 punto): convierte la línea y escribe solo el nombre del árbol. */
    private static Arbol ejercicio2LeerYEscribirArbol(Path entrada, Path salida) {
        Arbol arbol;
        // try-with-resources cierra el lector incluso si falla el análisis del JSON.
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            // El ejercicio solo utiliza el primer registro del fichero.
            String linea = lector.readLine();
            if (linea == null) {
                System.err.println("El fichero del árbol 1 está vacío: " + entrada);
                return null;
            }
            arbol = ParseadorArboles.parsear(linea);
            // El parseador devuelve null cuando la estructura es legible pero sus valores
            // incumplen las reglas del modelo Arbol.
            if (arbol == null) {
                System.err.println("El árbol 1 tiene valores inválidos.");
                return null;
            }
        } catch (FormatoJsonIncorrectoException | TipoDatoIncorrectoException e) {
            System.err.println("No se pudo interpretar el árbol 1: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
            return null;
        }

        Path ficheroSalida = salida.resolve(SALIDA_NOMBRE);
        try {
            // CREATE permite la primera ejecución; TRUNCATE_EXISTING sustituye el contenido
            // anterior para que el fichero tenga únicamente el nombre actual.
            Files.writeString(ficheroSalida, arbol.getNombre() + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            System.out.println("Nombre del árbol escrito en " + ficheroSalida);
        } catch (IOException e) {
            System.err.println("No se pudo escribir " + ficheroSalida + ": " + e.getMessage());
            // El árbol leído sigue disponible para el ejercicio 6.
        }
        return arbol;
    }

    /** Ejercicio 3 (2 puntos): una línea JSON por árbol y el ID como clave del mapa. */
    private static HashMap<Integer, Arbol> ejercicio3LeerArboles(Path entrada) {
        // La clave permite localizar un árbol por ID sin recorrer todos los registros.
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            // JSONL contiene un objeto JSON independiente en cada línea.
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                try {
                    Arbol arbol = ParseadorArboles.parsear(linea);
                    if (arbol == null) {
                        System.err.println("Fichero 2, línea " + numeroLinea + ": valores inválidos.");
                    } else if (arboles.putIfAbsent(arbol.getId(), arbol) != null) {
                        // Se conserva el primer árbol con ese ID y se descarta el duplicado.
                        System.err.println("Fichero 2, línea " + numeroLinea
                                + ": ID repetido " + arbol.getId() + ".");
                    }
                } catch (FormatoJsonIncorrectoException | TipoDatoIncorrectoException e) {
                    System.err.println("Fichero 2, línea " + numeroLinea + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
        }
        System.out.println("Árboles incorporados desde el fichero 2: " + arboles.size());
        return arboles;
    }

    /** Ejercicio 4 (1 punto): un entero binario de 32 bits y consulta por clave. */
    private static void ejercicio4LeerIdYMostrarArbol(
            Path ficheroId, HashMap<Integer, Arbol> arboles) {
        // readInt interpreta cuatro bytes en orden big-endian, no caracteres decimales.
        try (DataInputStream entrada = new DataInputStream(Files.newInputStream(ficheroId))) {
            int id = entrada.readInt();
            Arbol arbol = arboles.get(id);
            if (arbol == null) {
                System.out.println("No hay ningún árbol con ID " + id + ".");
            } else {
                System.out.println("Árbol con ID " + id + ": " + arbol);
            }
        } catch (EOFException e) {
            // Un fichero con menos de cuatro bytes no contiene el ID completo.
            System.err.println("El fichero binario no contiene un entero completo: " + ficheroId);
        } catch (IOException e) {
            System.err.println("No se pudo leer " + ficheroId + ": " + e.getMessage());
        }
    }

    /** Ejercicio 5 (2 puntos): descarta cada registro problemático sin detener la lectura. */
    private static HashMap<Integer, Arbol> ejercicio5LeerArbolesConErrores(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        try (BufferedReader lector = Files.newBufferedReader(entrada, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                // El try está dentro del bucle: un registro defectuoso no impide leer el siguiente.
                try {
                    Arbol arbol = ParseadorArboles.parsear(linea);
                    // null indica valores de dominio inválidos, distintos de los errores
                    // de sintaxis, propiedades o tipos que producen excepciones.
                    if (arbol == null) {
                        System.err.println("Fichero 3, línea " + numeroLinea
                                + ": valores de dominio inválidos; registro descartado.");
                    } else if (arboles.putIfAbsent(arbol.getId(), arbol) != null) {
                        System.err.println("Fichero 3, línea " + numeroLinea
                                + ": ID repetido " + arbol.getId() + "; registro descartado.");
                    }
                } catch (FormatoJsonIncorrectoException e) {
                    // Se distingue un JSON mal formado de un valor con el tipo equivocado.
                    System.err.println("Fichero 3, línea " + numeroLinea
                            + ": formato JSON incorrecto (" + e.getMessage() + "); registro descartado.");
                } catch (TipoDatoIncorrectoException e) {
                    System.err.println("Fichero 3, línea " + numeroLinea
                            + ": tipo de dato incorrecto (" + e.getMessage() + "); registro descartado.");
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + entrada + ": " + e.getMessage());
        }
        System.out.println("Árboles incorporados desde el fichero 3: " + arboles.size());
        return arboles;
    }

    /** Ejercicio 6 (3 puntos): primera creación exclusiva y actualización completa posterior. */
    private static void ejercicio6FusionarYGenerar(
            HashMap<Integer, Arbol> arboles2, HashMap<Integer, Arbol> arboles3,
            Arbol arbol1, Path salida) {
        // La ayuda conserva el árbol del fichero 2 cuando el mismo ID aparece en ambos mapas.
        // El árbol del fichero 1 todavía no se incorpora: eso corresponde a la Parte B.
        HashMap<Integer, Arbol> mapa = OperacionesArboles.combinar(arboles2, arboles3);
        Path ficheroSalida = salida.resolve(SALIDA_FINAL);

        // No se inicia la Parte B hasta que el escritor se haya cerrado con éxito.
        try (BufferedWriter escritor = Files.newBufferedWriter(ficheroSalida,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE)) {
            // CREATE_NEW evita sobrescribir una salida que ya existía. El mapa se ordena
            // antes de serializarlo porque HashMap no garantiza orden de recorrido.
            for (Arbol arbol : OperacionesArboles.ordenar(mapa)) {
                escritor.write(arbol.toJson());
                escritor.newLine();
            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("El fichero final ya existe; se conserva intacto: " + ficheroSalida);
            return;
        } catch (IOException e) {
            System.err.println("Falló la primera escritura de " + ficheroSalida
                    + "; no se realiza la actualización: " + e.getMessage());
            return;
        }
        System.out.println("Primera generación completada con " + mapa.size() + " árboles.");

        // La Parte B solo se alcanza si la Parte A ha creado y cerrado el fichero con éxito.
        // La ayuda incorpora el árbol del fichero 1 solo si falta su ID; acepta también null.
        OperacionesArboles.incorporarSiAusente(mapa, arbol1);
        try (BufferedWriter escritor = Files.newBufferedWriter(ficheroSalida,
                StandardCharsets.UTF_8, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            // Se reemplaza el contenido completo para evitar líneas sobrantes de la versión anterior.
            for (Arbol arbol : OperacionesArboles.ordenar(mapa)) {
                escritor.write(arbol.toJson());
                escritor.newLine();
            }
            System.out.println("Fichero final actualizado con " + mapa.size() + " árboles.");
        } catch (IOException e) {
            System.err.println("No se pudo actualizar " + ficheroSalida + ": " + e.getMessage());
        }
    }
}
