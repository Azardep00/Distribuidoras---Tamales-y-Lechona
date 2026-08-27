package pagos;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class WompiApiClient {

    private static final String LLAVE_PRIVADA = "prv_test_KX9g0vmoEQvTE2zYk7vBs0wSFmcILo9I";
    private static final String URL_BASE = "https://sandbox.wompi.co/v1/payment_links";

    public JSONObject crearLinkDePago(String nombre, String descripcion, long montoEnCentavos) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            JSONObject cuerpo = new JSONObject();
            cuerpo.put("name", nombre);
            cuerpo.put("description", descripcion);
            cuerpo.put("single_use", true);
            cuerpo.put("collect_shipping", false);
            cuerpo.put("currency", "COP");
            cuerpo.put("amount_in_cents", montoEnCentavos);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_BASE))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + LLAVE_PRIVADA)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta Wompi: " + response.body());
            return new JSONObject(response.body());

        } catch (Exception e) {
            System.out.println("Error al conectar con Wompi: " + e.getMessage());
            return null;
        }
    }
}