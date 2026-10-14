package es.iesluisbraille.accesodatos;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/** Modelo proporcionado: no se modifica en el examen. */
public final class Arbol implements Comparable<Arbol> {
    private final int id;
    private final String nombre;
    private final String especie;
    private final int edad;
    private final double altura;

    public Arbol(int id, String nombre, String especie, int edad, double altura) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre);
        this.especie = Objects.requireNonNull(especie);
        this.edad = edad;
        this.altura = altura;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public int getEdad() {
        return edad;
    }

    public double getAltura() {
        return altura;
    }

    /** Orden natural: especie, nombre e ID. No es el orden del HashMap. */
    @Override
    public int compareTo(Arbol otro) {
        return Comparator.comparing(Arbol::getEspecie, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Arbol::getNombre, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Arbol::getId)
                .compare(this, otro);
    }

    /** Un objeto JSON compacto, sin saltos de línea, con todas sus propiedades. */
    public String toJson() {
        return String.format(Locale.ROOT,
                "{\"id\":%d,\"nombre\":%s,\"especie\":%s,\"edad\":%d,\"altura\":%s}",
                id, JsonPlano.citar(nombre), JsonPlano.citar(especie), edad,
                Double.toString(altura));
    }

    /** Representación legible para el ejercicio de consulta por ID. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Árbol [ID=%d, nombre=%s, especie=%s, edad=%d años, altura=%.2f m]",
                id, nombre, especie, edad, altura);
    }
}
