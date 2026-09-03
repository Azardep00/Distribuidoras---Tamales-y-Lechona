package pagos;

import org.json.JSONObject;

public class AdaptadorPagoWompi implements ProcesadorPago {

    private final WompiApiClient client = new WompiApiClient();

    public String procesarPago(double montoTotal, String referenciaPedido) {
        if (montoTotal <= 0) {
            throw new IllegalArgumentException("El pedido debe tener un total mayor que 0.");
        }

        JSONObject r = client.crearLinkDePago(
                "Pedido " + referenciaPedido,
                "Pago de tamales y lechona",
                Math.round(montoTotal * 100)
        );

        if (r.has("data")) {
            JSONObject data = r.getJSONObject("data");
            if (data.has("checkout_url") && !data.getString("checkout_url").isBlank()) {
                return data.getString("checkout_url");
            }
            if (data.has("id")) {
                return "https://checkout.wompi.co/l/" + data.getString("id");
            }
        }

        throw new IllegalStateException("Respuesta de Wompi sin enlace de checkout.");
    }
}