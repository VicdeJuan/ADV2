package es.iesluisbraille.accesodatos;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/** Clase proporcionada. No hay que modificarla en el examen. */
public final class Alumno implements Comparable<Alumno> {
    private final int id;
    private final String nombre;
    private final String apellidos;
    private final int edad;
    private final double notaMedia;

    public Alumno(int id, String nombre, String apellidos, int edad, double notaMedia) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre);
        this.apellidos = Objects.requireNonNull(apellidos);
        this.edad = edad;
        this.notaMedia = notaMedia;
    }

    /** Identificador único: dos alumnos con el mismo ID se consideran repetidos. */
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public double getNotaMedia() {
        return notaMedia;
    }

    /** Orden natural: apellidos, después nombre y, si empatan, ID. */
    @Override
    public int compareTo(Alumno otro) {
        return Comparator.comparing(Alumno::getApellidos, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Alumno::getNombre, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Alumno::getId)
                .compare(this, otro);
    }

    /** Formato CSV entregado: id;nombre;apellidos;edad;notaMedia (decimal con punto). */
    public String toCsv() {
        return String.format(Locale.ROOT, "%d;%s;%s;%d;%.2f",
                id, nombre, apellidos, edad, notaMedia);
    }

    @Override
    public String toString() {
        return toCsv();
    }
}
