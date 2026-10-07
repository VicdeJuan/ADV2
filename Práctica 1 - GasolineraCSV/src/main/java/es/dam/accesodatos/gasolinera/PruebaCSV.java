package es.dam.accesodatos.gasolinera;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PruebaCSV implements Almacenamiento{

    private final Path clientes;
    private final Path pagos;

    public PruebaCSV(Path directorio) {
        clientes = directorio.resolve("clientes.csv");

        pagos = directorio.resolve("pagos.csv");
        if (! Files.isReadable(pagos) || ! Files.isReadable(clientes))
            System.exit(-1);
    }

    @Override
    public List<Cliente> cargarClientes() throws ErrorAlmacenamiento {
        List<Cliente> retval = new ArrayList<>();
        Cliente toadd;
        try (BufferedReader bf = Files.newBufferedReader(clientes)){
            String linea;
            String leido[];
            int id;
            String nombre,telefono,matricula;
            while( (linea = bf.readLine()) != null){
                leido = linea.split(",");
                id = Integer.parseInt(leido[0]);
                nombre = leido[1];
                telefono = leido[2];
                matricula = leido[3];
                retval.add(new Cliente(id,nombre,telefono,matricula));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return retval;
    }

    @Override
    public List<Pago> cargarPagos() throws ErrorAlmacenamiento {
        return List.of();
    }

    @Override
    public void guardarCliente(Cliente cliente) throws ErrorAlmacenamiento {

    }

    @Override
    public void guardarPago(Pago pago) throws ErrorAlmacenamiento {

    }
}
