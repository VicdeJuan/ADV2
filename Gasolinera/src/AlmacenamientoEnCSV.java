import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class AlmacenamientoEnCSV implements Almacenamiento {

    Path directorio;
    Path archivoClientes;
    Path archivoPagos;

    public AlmacenamientoEnCSV() throws IOException {
        directorio = Path.of("datos");

        Files.createDirectory(directorio);
        archivoClientes = directorio.resolve("clientes.csv"); // crear la ruta datos/clientes.csv
        archivoPagos = directorio.resolve("pagos.csv");


    }

    @Override
    public List<Cliente> leerClientes() {

        try(BufferedReader bf = Files.newBufferedReader(archivoClientes);)
        {

        }catch (IOException e){
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public boolean escribirCliente(Cliente cliente) {
        try(BufferedWriter bw = Files.newBufferedWriter(archivoClientes, StandardOpenOption.CREATE_NEW){

            // Respetar el formato CSV.
            bw.write(cliente.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<Pago> leerPagos() {
        return List.of();
    }

    @Override
    public boolean escribirPago(Pago pago) {
        return false;
    }
}
