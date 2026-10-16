package com.tamaleslechona.tamaleslechona.client;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.tamaleslechona.tamaleslechona.dto.ClimaResponse;

@Component
public class ClimaClient {

    private static final Logger log = LoggerFactory.getLogger(ClimaClient.class);
    private static final double LAT = 4.44;
    private static final double LON = -75.24;
    // El clima no cambia cada segundo: se reutiliza 10 minutos para no agotar los
    // limites diarios de las APIs gratuitas.
    private static final Duration VIGENCIA = Duration.ofMinutes(10);
    // Si todos los proveedores fallan, se sirve el ultimo dato bueno hasta por 6 horas.
    private static final Duration MAXIMO_VIEJO = Duration.ofHours(6);

    private final RestClient principal;
    private final RestClient respaldo;
    private ClimaResponse ultimo;
    private Instant ultimoEn;

    public ClimaClient(@Qualifier("climaRestClient") RestClient principal,
                       @Qualifier("climaRespaldoRestClient") RestClient respaldo) {
        this.principal = principal;
        this.respaldo = respaldo;
    }

    public synchronized ClimaResponse consultarClima() {
        Instant ahora = Instant.now();
        if (ultimo != null && ultimoEn.plus(VIGENCIA).isAfter(ahora)) {
            return ultimo;
        }
        ClimaResponse nuevo = null;
        try {
            nuevo = consultarOpenMeteo();
        } catch (RuntimeException e) {
            log.warn("Open-Meteo fallo ({}). Probando proveedor de respaldo.", e.toString());
        }
        if (nuevo == null) {
            try {
                nuevo = consultarMetNo();
            } catch (RuntimeException e) {
                log.warn("MET Norway tambien fallo: {}", e.toString());
            }
        }
        if (nuevo != null) {
            ultimo = nuevo;
            ultimoEn = ahora;
            return nuevo;
        }
        if (ultimo != null && ultimoEn.plus(MAXIMO_VIEJO).isAfter(ahora)) {
            return ultimo;
        }
        throw new IllegalStateException("Ningun proveedor de clima respondio");
    }

    private ClimaResponse consultarOpenMeteo() {
        ClimaResponse r = principal.get()
                .uri("/v1/forecast?latitude=" + LAT + "&longitude=" + LON + "&current_weather=true")
                .retrieve()
                .body(ClimaResponse.class);
        if (r == null || r.current_weather() == null) {
            throw new IllegalStateException("Respuesta de Open-Meteo sin datos");
        }
        return r;
    }

    // MET Norway devuelve m/s y un codigo de simbolo; se convierte al mismo formato
    // que Open-Meteo (km/h y codigo WMO aproximado) para no tocar el frontend.
    private ClimaResponse consultarMetNo() {
        JsonNode raiz = respaldo.get()
                .uri("/weatherapi/locationforecast/2.0/compact?lat=" + LAT + "&lon=" + LON)
                .retrieve()
                .body(JsonNode.class);
        JsonNode primero = raiz == null ? null : raiz.path("properties").path("timeseries").path(0).path("data");
        JsonNode det = primero == null ? null : primero.path("instant").path("details");
        if (det == null || !det.has("air_temperature")) {
            throw new IllegalStateException("Respuesta de MET Norway sin datos");
        }
        double temp = det.path("air_temperature").asDouble();
        double viento = det.path("wind_speed").asDouble() * 3.6;
        String simbolo = primero.path("next_1_hours").path("summary").path("symbol_code").asText("");
        return new ClimaResponse(LAT, LON,
                new ClimaResponse.CurrentWeather(temp, Math.round(viento * 10) / 10.0, aCodigoWmo(simbolo)));
    }

    private static int aCodigoWmo(String s) {
        if (s.contains("thunder")) return 95;
        if (s.contains("snow") || s.contains("sleet")) return 71;
        if (s.contains("rain")) return 61;
        if (s.contains("fog")) return 45;
        if (s.startsWith("partlycloudy") || s.startsWith("fair")) return 2;
        if (s.startsWith("cloudy")) return 3;
        return 0;
    }
}
