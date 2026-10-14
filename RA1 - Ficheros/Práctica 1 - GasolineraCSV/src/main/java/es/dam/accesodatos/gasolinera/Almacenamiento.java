package es.dam.accesodatos.gasolinera;

import java.util.List;

/**
 * Frontera de persistencia: la aplicación no conoce ni rutas ni sintaxis CSV.
 * Otra implementación podría usar JSON, una base de datos o un servicio remoto.
 */
public interface Almacenamiento {
    /**
     * Prepara el soporte físico antes de intentar cargar o guardar datos.
     * Una implementación sin recursos que crear puede conservar este comportamiento vacío.
     */
    default void preparar() throws ErrorAlmacenamiento {
        // Sin preparación por defecto.
    }

    List<Cliente> cargarClientes() throws ErrorAlmacenamiento;

    List<Pago> cargarPagos() throws ErrorAlmacenamiento;

    void guardarCliente(Cliente cliente) throws ErrorAlmacenamiento;

    void guardarPago(Pago pago) throws ErrorAlmacenamiento;
}
