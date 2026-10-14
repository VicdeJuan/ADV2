import java.util.List;

public interface Almacenamiento {

    public List<Cliente> leerClientes();
    public boolean escribirCliente(Cliente cliente);

    public List<Pago> leerPagos();
    public boolean escribirPago(Pago pago);

}
