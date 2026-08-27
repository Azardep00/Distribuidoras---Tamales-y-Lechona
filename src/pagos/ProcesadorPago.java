package pagos;

public interface ProcesadorPago {
    String procesarPago(double montoTotal, String referenciaPedido);
    // devuelve el link de pago generado (o null si falla)
}