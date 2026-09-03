package pagos;

import java.net.URI;
import java.net.http.*;
import java.util.regex.*;

public class WompiApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;

    public WompiApiClient() {
        baseUrl = System.getenv().getOrDefault("WOMPI_BASE_URL", "https://sandbox.wompi.co/v1");
    }

    public String crearLinkDePago(String nombre, String descripcion, long cents) {
        String key = System.getenv("WOMPI_PRIVATE_KEY");
        if (key == null || key.isBlank())
            throw new IllegalStateException(
                    "Configura WOMPI_PRIVATE_KEY para generar el enlace de pago.");
        String body =
                "{\"name\":\""
                        + json(nombre)
                        + "\",\"description\":\""
                        + json(descripcion)
                        + "\",\"single_use\":true,\"collect_shipping\":false,\"currency\":\"COP\",\"amount_in_cents\":"
                        + cents
                        + "}";
        try {
            HttpRequest req =
                    HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/payment_links"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + key)
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();
            HttpResponse<String> r = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (r.statusCode() < 200 || r.statusCode() >= 300)
                throw new IllegalStateException("Wompi HTTP " + r.statusCode() + ": " + r.body());
            String url = find(r.body(), "checkout_url");
            if (url != null && !url.isBlank()) return url;
            String id = find(r.body(), "id");
            if (id != null && !id.isBlank()) return "https://checkout.wompi.co/l/" + id;
            throw new IllegalStateException("Wompi no devolvió un checkout.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Solicitud a Wompi interrumpida.", e);
        } catch (Exception e) {
            if (e instanceof IllegalStateException x) throw x;
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private String find(String json, String key) {
        Matcher m =
                Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"")
                        .matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private String json(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
