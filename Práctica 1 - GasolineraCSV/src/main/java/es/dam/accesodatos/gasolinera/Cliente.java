package es.dam.accesodatos.gasolinera;

/** Datos inmutables de un cliente ya validado. */
public record Cliente(int id, String nombre, String telefono, String matricula) {
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getMatricula() {
        return matricula;
    }
}
