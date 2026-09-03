package pagos;

public class AdaptadorPagoWompi implements ProcesadorPago {
    private final WompiApiClient client = new WompiApiClient();

    public String procesarPago(long cents, String ref) {
        if (cents <= 0) throw new IllegalArgumentException("El valor debe ser mayor que cero.");
        return client.crearLinkDePago("Pedido " + ref, "Pago de tamales y lechona", cents);
    }
}
