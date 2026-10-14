package es.iesluisbraille.accesodatos;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/** Conversión JSON proporcionada. No se implementa ni modifica en el examen. */
public final class ParseadorArboles {
    private static final Set<String> PROPIEDADES = Set.of(
            "id", "nombre", "especie", "edad", "altura");

    private ParseadorArboles() {
    }

    /**
     * Devuelve un árbol, o null si la línea es null o contiene valores
     * de dominio inválidos: ID no positivo, nombre/especie vacíos,
     * edad negativa, altura no positiva o no finita.
     * Sintaxis o esquema incorrectos: FormatoJsonIncorrectoException.
     * Tipos incorrectos (incluido un valor JSON null): TipoDatoIncorrectoException.
     */
    public static Arbol parsear(String linea)
            throws FormatoJsonIncorrectoException, TipoDatoIncorrectoException {
        if (linea == null) {
            return null;
        }
        Map<String, Object> datos = JsonPlano.leer(linea);
        if (!datos.keySet().equals(PROPIEDADES)) {
            throw new FormatoJsonIncorrectoException(
                    "El objeto debe contener exactamente id, nombre, especie, edad y altura");
        }
        int id = entero(datos, "id");
        String nombre = cadena(datos, "nombre").trim();
        String especie = cadena(datos, "especie").trim();
        int edad = entero(datos, "edad");
        double altura = numero(datos, "altura").doubleValue();
        if (id <= 0 || nombre.isEmpty() || especie.isEmpty() || edad < 0
                || !Double.isFinite(altura) || altura <= 0) {
            return null;
        }
        return new Arbol(id, nombre, especie, edad, altura);
    }

    private static String cadena(Map<String, Object> datos, String clave)
            throws TipoDatoIncorrectoException {
        if (datos.get(clave) instanceof String texto) {
            return texto;
        }
        throw new TipoDatoIncorrectoException(clave + " debe ser una cadena JSON");
    }

    private static BigDecimal numero(Map<String, Object> datos, String clave)
            throws TipoDatoIncorrectoException {
        if (datos.get(clave) instanceof BigDecimal valor) {
            return valor;
        }
        throw new TipoDatoIncorrectoException(clave + " debe ser un número JSON");
    }

    private static int entero(Map<String, Object> datos, String clave)
            throws TipoDatoIncorrectoException {
        try {
            return numero(datos, clave).intValueExact();
        } catch (ArithmeticException e) {
            throw new TipoDatoIncorrectoException(clave + " debe ser un entero de 32 bits");
        }
    }
}
