package es.iesluisbraille.accesodatos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/** Lógica de integración resuelta. No realiza operaciones de entrada/salida. */
public final class OperacionesArboles {
    private OperacionesArboles() {
    }

    /** Copia ambos mapas; en IDs coincidentes conserva el árbol del primero. */
    public static HashMap<Integer, Arbol> combinar(
            HashMap<Integer, Arbol> primero, HashMap<Integer, Arbol> segundo) {
        HashMap<Integer, Arbol> resultado = new HashMap<>(primero);
        segundo.forEach(resultado::putIfAbsent);
        return resultado;
    }

    /** Incorpora el árbol solo si no es null y su ID todavía no está presente. */
    public static void incorporarSiAusente(HashMap<Integer, Arbol> mapa, Arbol arbol) {
        if (arbol != null) {
            mapa.putIfAbsent(arbol.getId(), arbol);
        }
    }

    /** Devuelve una copia ordenada para recorrerla al escribir, sin alterar el mapa. */
    public static Iterable<Arbol> ordenar(HashMap<Integer, Arbol> mapa) {
        ArrayList<Arbol> ordenados = new ArrayList<>(mapa.values());
        Collections.sort(ordenados);
        return ordenados;
    }
}
