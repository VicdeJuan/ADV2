package es.dam.accesodatos.gasolinera;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Reglas de negocio y colección en memoria de la sesión. */
public final class ServicioGasolinera {
    private final Almacenamiento almacenamiento;
    private final List<Cliente> clientes;
    private final List<Pago> pagos;

    public ServicioGasolinera(Almacenamiento almacenamiento) throws ErrorAlmacenamiento {
        this.almacenamiento = almacenamiento;
        clientes = new ArrayList<>(almacenamiento.cargarClientes());
        pagos = new ArrayList<>(almacenamiento.cargarPagos());
        comprobarReferencias();
    }

    public Cliente altaCliente(String nombre, String telefono, String matricula) throws ErrorAlmacenamiento {
        String matriculaNormalizada = matricula.trim().toUpperCase(Locale.ROOT);
        for (Cliente clienteExistente : clientes) {
            if (clienteExistente.matricula().equalsIgnoreCase(matriculaNormalizada)) {
                throw new IllegalArgumentException("Esa matrícula ya está registrada.");
            }
        }

        Cliente cliente = new Cliente(siguienteIdCliente(), nombre.trim(), telefono.trim(), matriculaNormalizada);
        // Solo se incorpora tras persistir correctamente: no se confirma un alta fallida.
        almacenamiento.guardarCliente(cliente);
        clientes.add(cliente);
        return cliente;
    }

    public Pago registrarPago(int clienteId, LocalDate fecha, BigDecimal importe, BigDecimal litros,
                              String combustible) throws ErrorAlmacenamiento {
        if (buscarClientePorId(clienteId) == null) {
            throw new IllegalArgumentException("No existe ese cliente.");
        }

        Pago pago = new Pago(siguienteIdPago(), clienteId, fecha, importe, litros, combustible.trim());
        almacenamiento.guardarPago(pago);
        pagos.add(pago);
        return pago;
    }

    public List<Cliente> clientesOrdenados() {
        List<Cliente> resultado = new ArrayList<>(clientes);
        Collections.sort(resultado, new Comparator<Cliente>() {
            @Override
            public int compare(Cliente primero, Cliente segundo) {
                int porNombre = primero.nombre().compareToIgnoreCase(segundo.nombre());
                if (porNombre != 0) {
                    return porNombre;
                }
                return Integer.compare(primero.id(), segundo.id());
            }
        });
        return resultado;
    }

    public List<Cliente> buscarClientes(String texto) {
        String consulta = texto.toLowerCase(Locale.ROOT);
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente cliente : clientesOrdenados()) {
            if (contiene(cliente.nombre(), consulta) || contiene(cliente.telefono(), consulta)
                    || contiene(cliente.matricula(), consulta)) {
                resultado.add(cliente);
            }
        }
        return resultado;
    }

    public List<Pago> pagosOrdenados() {
        List<Pago> resultado = new ArrayList<>(pagos);
        Collections.sort(resultado, new Comparator<Pago>() {
            @Override
            public int compare(Pago primero, Pago segundo) {
                int porFecha = segundo.fecha().compareTo(primero.fecha());
                if (porFecha != 0) {
                    return porFecha;
                }
                return Integer.compare(segundo.id(), primero.id());
            }
        });
        return resultado;
    }

    /** Devuelve null si no hay cliente con ese identificador. */
    public Cliente buscarClientePorId(int id) {
        for (Cliente cliente : clientes) {
            if (cliente.id() == id) {
                return cliente;
            }
        }
        return null;
    }

    public boolean hayClientes() {
        return !clientes.isEmpty();
    }

    private void comprobarReferencias() throws ErrorAlmacenamiento {
        Set<Integer> idsClientes = new HashSet<>();
        Set<String> matriculas = new HashSet<>();
        for (Cliente cliente : clientes) {
            if (!idsClientes.add(cliente.id())) {
                throw new ErrorAlmacenamiento("Hay identificadores de cliente duplicados. No se modificará ningún fichero.");
            }
            if (!matriculas.add(cliente.matricula().toUpperCase(Locale.ROOT))) {
                throw new ErrorAlmacenamiento("Hay matrículas duplicadas en el fichero de clientes. No se modificará ningún fichero.");
            }
        }

        Set<Integer> idsPagos = new HashSet<>();
        for (Pago pago : pagos) {
            if (!idsPagos.add(pago.id())) {
                throw new ErrorAlmacenamiento("Hay identificadores de pago duplicados. No se modificará ningún fichero.");
            }
            if (buscarClientePorId(pago.clienteId()) == null) {
                throw new ErrorAlmacenamiento("Hay un pago que referencia a un cliente inexistente. No se modificará ningún fichero.");
            }
        }
    }

    private int siguienteIdCliente() {
        int maximo = 0;
        for (Cliente cliente : clientes) {
            if (cliente.id() > maximo) {
                maximo = cliente.id();
            }
        }
        return maximo + 1;
    }

    private int siguienteIdPago() {
        int maximo = 0;
        for (Pago pago : pagos) {
            if (pago.id() > maximo) {
                maximo = pago.id();
            }
        }
        return maximo + 1;
    }

    private boolean contiene(String valor, String consulta) {
        return valor.toLowerCase(Locale.ROOT).contains(consulta);
    }
}
