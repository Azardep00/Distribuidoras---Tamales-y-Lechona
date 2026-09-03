package pagos;

public interface ProcesadorPago {
    String procesarPago(long montoEnCentavos, String referenciaPedido);
}
