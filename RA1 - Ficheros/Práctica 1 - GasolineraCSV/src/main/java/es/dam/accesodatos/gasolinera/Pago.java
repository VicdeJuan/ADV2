package es.dam.accesodatos.gasolinera;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Un repostaje ya abonado; no representa un cobro bancario. */
public record Pago(int id, int clienteId, LocalDate fecha, BigDecimal importe,
                   BigDecimal litros, String combustible) {
    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public BigDecimal getLitros() {
        return litros;
    }

    public String getCombustible() {
        return combustible;
    }
}
