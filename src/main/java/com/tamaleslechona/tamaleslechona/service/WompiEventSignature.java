package com.tamaleslechona.tamaleslechona.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;

final class WompiEventSignature {

    private WompiEventSignature() {
    }

    static void validar(JsonNode evento, String checksumHeader, String eventsSecret) {
        JsonNode firma = evento.path("signature");
        JsonNode propiedades = firma.path("properties");
        String checksum = firma.path("checksum").asText();
        String timestamp = evento.path("timestamp").asText();

        if (!propiedades.isArray() || propiedades.isEmpty()
                || timestamp.isBlank() || !checksum.matches("[A-Fa-f0-9]{64}")) {
            throw new IllegalArgumentException("La firma del evento Wompi está incompleta.");
        }

        boolean idTransaccionFirmado = false;
        StringBuilder cadena = new StringBuilder();
        JsonNode datos = evento.path("data");
        for (JsonNode propiedad : propiedades) {
            String ruta = propiedad.asText();
            idTransaccionFirmado |= "transaction.id".equals(ruta);
            JsonNode valor = resolverRuta(datos, ruta);
            if (valor.isMissingNode() || valor.isNull()) {
                throw new IllegalArgumentException("El evento Wompi no contiene una propiedad firmada.");
            }
            cadena.append(valor.asText());
        }
        if ("transaction.updated".equals(evento.path("event").asText()) && !idTransaccionFirmado) {
            throw new IllegalArgumentException("La firma del evento no protege el identificador de la transacción.");
        }
        cadena.append(timestamp).append(eventsSecret);

        String calculado = sha256(cadena.toString());
        if (!compararSeguro(calculado, checksum)
                || (checksumHeader != null && !compararSeguro(calculado, checksumHeader))) {
            throw new IllegalArgumentException("La firma del evento Wompi no es válida.");
        }
    }

    static String sha256(String valor) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible en este entorno.", e);
        }
    }

    private static JsonNode resolverRuta(JsonNode raiz, String ruta) {
        JsonNode valor = raiz;
        for (String segmento : ruta.split("\\.")) {
            valor = valor.path(segmento);
        }
        return valor;
    }

    private static boolean compararSeguro(String esperado, String recibido) {
        return MessageDigest.isEqual(
                esperado.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII),
                recibido.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
    }
}
