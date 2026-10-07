package es.dam.accesodatos.gasolinera;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Interfaz de usuario por consola. Solicita y presenta datos, pero delega las
 * reglas de negocio y la persistencia en el servicio recibido.
 */
public final class AplicacionConsola {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private final ServicioGasolinera servicio;
    private final Scanner entrada = new Scanner(System.in);

    public AplicacionConsola(ServicioGasolinera servicio) {
        this.servicio = servicio;
    }

    public void ejecutar() {
        String opcion;
        do {
            System.out.print("\n=== GESTIÓN DE GASOLINERA ===\n1. Dar de alta un cliente\n2. Listar clientes\n3. Buscar clientes\n4. Procesar un pago de repostaje\n5. Consultar pagos\n0. Salir\nOpción: ");
            opcion = entrada.nextLine().trim();
            try {
                switch (opcion) {
                    case "1":
                        altaCliente();
                        break;
                    case "2":
                        mostrarClientes(servicio.clientesOrdenados());
                        break;
                    case "3":
                        buscarClientes();
                        break;
                    case "4":
                        procesarPago();
                        break;
                    case "5":
                        mostrarPagos();
                        break;
                    case "0":
                        System.out.println("Hasta pronto.");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (ErrorAlmacenamiento e) {
                System.out.println("Error de almacenamiento: " + e.getMessage());
                return;
            }
        } while (!opcion.equals("0"));
    }

    private void altaCliente() throws ErrorAlmacenamiento {
        String nombre = obligatorio("Nombre: ");
        String telefono = obligatorio("Teléfono: ");
        String matricula = obligatorio("Matrícula: ");
        try {
            Cliente cliente = servicio.altaCliente(nombre, telefono, matricula);
            System.out.println("Cliente registrado con ID " + cliente.id() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + " No se ha creado el cliente.");
        }
    }

    private void buscarClientes() {
        String texto = obligatorio("Texto que buscar: ");
        mostrarClientes(servicio.buscarClientes(texto));
    }

    private void procesarPago() throws ErrorAlmacenamiento {
        if (!servicio.hayClientes()) {
            System.out.println("Primero debe darse de alta un cliente.");
            return;
        }
        mostrarClientes(servicio.clientesOrdenados());
        int clienteId = enteroPositivo("ID del cliente: ");
        Cliente cliente = servicio.buscarClientePorId(clienteId);
        if (cliente == null) {
            System.out.println("No existe un cliente con ese identificador. No se ha registrado el pago.");
            return;
        }
        LocalDate fecha = pedirFecha();
        BigDecimal importe = pedirCantidad("Importe (€): ");
        BigDecimal litros = pedirCantidad("Litros: ");
        String combustible = obligatorio("Combustible: ");
        Pago pago = servicio.registrarPago(clienteId, fecha, importe, litros, combustible);
        System.out.printf(Locale.ROOT, "Pago %d registrado para %s: %.2f €.\n", pago.id(), cliente.nombre(), pago.importe());
    }

    private void mostrarClientes(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("No se han encontrado clientes.");
            return;
        }
        System.out.println("ID   NOMBRE                 TELÉFONO          MATRÍCULA");
        for (Cliente cliente : clientes) {
            System.out.printf("%-4d %-22s %-17s %s%n", cliente.id(), cliente.nombre(), cliente.telefono(), cliente.matricula());
        }
    }

    private void mostrarPagos() {
        List<Pago> pagos = servicio.pagosOrdenados();
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        System.out.println("ID   CLIENTE                FECHA        IMPORTE    LITROS   COMBUSTIBLE");
        for (Pago pago : pagos) {
            Cliente cliente = servicio.buscarClientePorId(pago.clienteId());
            System.out.printf(Locale.ROOT, "%-4d %-22s %-12s %8.2f € %8.2f %s%n", pago.id(),
                    cliente.nombre(), FECHA.format(pago.fecha()), pago.importe(), pago.litros(), pago.combustible());
        }
    }

    private String obligatorio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("Este campo es obligatorio.");
        }
    }

    private int enteroPositivo(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                int numero = Integer.parseInt(entrada.nextLine().trim());
                if (numero > 0) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                // El mensaje común se muestra también cuando el número es cero o negativo.
            }
            System.out.println("Introduce un entero positivo.");
        }
    }

    private LocalDate pedirFecha() {
        while (true) {
            System.out.print("Fecha (dd/MM/aaaa; vacío para hoy): ");
            String texto = entrada.nextLine().trim();
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto, FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("La fecha no es válida.");
            }
        }
    }

    private BigDecimal pedirCantidad(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                BigDecimal cantidad = new BigDecimal(entrada.nextLine().trim().replace(',', '.'));
                if (cantidad.signum() > 0 && cantidad.scale() <= 2) {
                    return cantidad;
                }
            } catch (NumberFormatException e) {
                // Se muestra el mismo mensaje para un texto no numérico y una cantidad inválida.
            }
            System.out.println("Introduce una cantidad mayor que cero y con un máximo de dos decimales.");
        }
    }
}
