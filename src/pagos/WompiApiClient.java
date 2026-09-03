package pagos;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.*;

/** Cliente Wompi sin secretos dentro del código fuente. Usa WOMPI_PRIVATE_KEY. */
public class WompiApiClient {

    private static final String URL = "https://sandbox.wompi.co/v1/payment_links";
    private final HttpClient client = HttpClient.newHttpClient();

    public JSONObject crearLinkDePago(String nombre, String descripcion, long cents) {
        String key = System.getenv("WOMPI_PRIVATE_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno WOMPI_PRIVATE_KEY (llave sandbox de Wompi).");
        }

        try {
            JSONObject body = new JSONObject();
            body.put("name", nombre);
            body.put("description", descripcion);
            body.put("single_use", true);
            body.put("collect_shipping", false);
            body.put("currency", "COP");
            body.put("amount_in_cents", cents);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + key)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                throw new IllegalStateException("Wompi respondió HTTP " + res.statusCode() + ": " + res.body());
            }

            return new JSONObject(res.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La solicitud a Wompi fue interrumpida.", e);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}