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

/** Solo se completan los seis métodos TODO. Consola y coordinación resueltas. */
public final class ExamenRA1B {
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
            System.out.print("Introduce la ruta del directorio de salida: ");
            String ruta = teclado.nextLine().trim();
            if (ruta.isEmpty()) {
                System.err.println("La ruta de salida no puede estar vacía.");
                return;
            }
            Path salida;
            try {
                salida = Path.of(ruta);
            } catch (InvalidPathException e) {
                System.err.println("Ruta de salida incorrecta: " + e.getMessage());
                return;
            }
            if (!prepararDirectorioSalida(salida)) {
                System.err.println("No se puede continuar sin un directorio válido.");
                return;
            }

            Arbol arbol1 = ejercicio2LeerYEscribirArbol(FICHERO_1, salida);
            HashMap<Integer, Arbol> arboles2 = ejercicio3LeerArboles(FICHERO_2);
            ejercicio4LeerIdYMostrarArbol(FICHERO_ID, arboles2);
            HashMap<Integer, Arbol> arboles3 = ejercicio5LeerArbolesConErrores(FICHERO_3);
            ejercicio6FusionarYGenerar(arboles2, arboles3, arbol1, salida);
        }
    }

    /** Ejercicio 1 (1 punto): comprobar/crear directorio e informar de errores. */
    private static boolean prepararDirectorioSalida(Path salida) {
        if (Files.notExists(salida)){
            try {
                Files.createDirectories(salida);
            } catch (IOException e) {
                System.out.printf("Error al crear el directorio (%s)\n", e.toString());
            }
            return true;
        } else if (!Files.isDirectory(salida)) {
            System.out.println("Existe un fichero con ese nombre");
            return false;
        }else{
            System.out.println("El directorio ya existe");
            return false;
        }
    }

    /** Ejercicio 2 (1 punto): leer el árbol y escribir su nombre en arbol1.txt. */
    private static Arbol ejercicio2LeerYEscribirArbol(Path entrada, Path salida) {
        // TODO: leer en UTF-8 y usar ParseadorArboles.parsear(linea).
        // Escribir getNombre() y un salto de línea. Devolver el árbol válido.
        // Si falla la escritura, conservar el árbol leído para el ejercicio 6.
        try (BufferedReader bf = Files.newBufferedReader(entrada);){
            Arbol a = ParseadorArboles.parsear(bf.readLine());
            if (a != null) {
                Files.writeString(salida, a.getNombre(), StandardCharsets.UTF_8);
            } else {
                System.out.printf("Arbol nulo");
            }
            return a;
        } catch (IOException e) {
            e.printStackTrace();;
        } catch (FormatoJsonIncorrectoException e) {
            System.out.println("Formato incorrecto en el json");
        } catch (TipoDatoIncorrectoException e) {
            System.out.println("Tipo de dato incorrecto");
        }
        return null;
    }

    /** Ejercicio 3 (2 puntos): leer 200 objetos JSON en un HashMap por ID. */
    private static HashMap<Integer, Arbol> ejercicio3LeerArboles(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        String linea;
        try(BufferedReader bf = Files.newBufferedReader(entrada)){
            while( (linea = bf.readLine()) != null)
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: leer línea a línea en UTF-8, parsear e incorporar al mapa.
        // La clave es getId(). Gestionar recursos y errores, e informar del total.
        return arboles;
    }

    /** Ejercicio 4 (1 punto): leer un entero binario y mostrar el árbol con ese ID. */
    private static void ejercicio4LeerIdYMostrarArbol(
            Path ficheroId, HashMap<Integer, Arbol> arboles) {
        // TODO: leer un entero binario de 32 bits (formato de DataInputStream.readInt).
        // Buscar por clave y mostrar el árbol con su toString(), o indicar que no existe.
        // Gestionar recursos y errores, incluido un fichero incompleto.
    }

    /** Ejercicio 5 (2 puntos): errores por línea y duplicados dentro del fichero 3. */
    private static HashMap<Integer, Arbol> ejercicio5LeerArbolesConErrores(Path entrada) {
        HashMap<Integer, Arbol> arboles = new HashMap<>();
        // TODO: leer en UTF-8 y tratar separadamente las dos excepciones y null.
        // Conservar la primera aparición de cada ID. Informar de línea y causa
        // para cada descarte; continuar con la siguiente línea. Mostrar el total.
        return arboles;
    }

    /** Ejercicio 6 (3 puntos): CREATE_NEW y actualización completa del fichero. */
    private static void ejercicio6FusionarYGenerar(
            HashMap<Integer, Arbol> arboles2, HashMap<Integer, Arbol> arboles3,
            Arbol arbol1, Path salida) {
        // TODO: usar OperacionesArboles.combinar(arboles2, arboles3).
        // Recorrer OperacionesArboles.ordenar(mapa) y escribir toJson() por línea.
        // Primera escritura: SALIDA_FINAL, UTF-8 y CREATE_NEW.
        // Si ya existe o la primera escritura falla, no realizar la Parte B.
        // Solo tras crear Y terminar la primera escritura: llamar a
        // incorporarSiAusente(mapa, arbol1), ordenar y reescribir todo truncando.
    }
}
