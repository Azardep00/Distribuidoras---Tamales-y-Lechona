package pagos;

import org.json.JSONObject;

public class AdaptadorPagoWompi implements ProcesadorPago {

    private WompiApiClient wompiClient;

    public AdaptadorPagoWompi() {
        this.wompiClient = new WompiApiClient();
    }

    @Override
    public String procesarPago(double montoTotal, String referenciaPedido) {
        // 1. Traducimos el monto: de "pesos" (double) a "centavos" (long), como lo pide Wompi
        long montoEnCentavos = (long) (montoTotal * 100);

        // 2. Llamamos a la API real a través del cliente
        JSONObject respuesta = wompiClient.crearLinkDePago(
                "Pedido " + referenciaPedido,
                "Pago de pedido de tamales y lechona",
                montoEnCentavos
        );

        // 3. Traducimos la respuesta de Wompi a algo simple que tu sistema entiende: el link o null
        if (respuesta != null && respuesta.has("data")) {
            String idLink = respuesta.getJSONObject("data").getString("id");
            return "https://checkout.wompi.co/l/" + idLink;
        } else {
            System.out.println("No se pudo generar el link de pago.");
            return null;
        }
    }
}