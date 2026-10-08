package com.tamaleslechona.tamaleslechona.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class WompiEventSignatureTest {

    private static final String EVENT_SECRET = "test_events_secret";
    private static final String TRANSACTION_ID = "11111111-1111-4111-8111-111111111111";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void acceptsAuthenticEventChecksum() throws Exception {
        JsonNode evento = eventoFirmado();
        String checksum = evento.path("signature").path("checksum").asText();

        assertDoesNotThrow(() -> WompiEventSignature.validar(evento, checksum, EVENT_SECRET));
    }

    @Test
    void rejectsEventWhenSignedTransactionIdWasChanged() throws Exception {
        JsonNode evento = eventoFirmado();
        String checksumOriginal = evento.path("signature").path("checksum").asText();
        ((ObjectNode) evento.path("data").path("transaction"))
                .put("id", "22222222-2222-4222-8222-222222222222");

        assertThrows(
                IllegalArgumentException.class,
                () -> WompiEventSignature.validar(evento, checksumOriginal, EVENT_SECRET));
    }

    private JsonNode eventoFirmado() throws Exception {
        String timestamp = "1791468000";
        JsonNode evento = objectMapper.readTree("""
                {
                  "event":"transaction.updated",
                  "timestamp":%s,
                  "data":{"transaction":{
                    "id":"%s",
                    "reference":"event-reference",
                    "status":"APPROVED",
                    "amount_in_cents":125000
                  }},
                  "signature":{
                    "properties":["transaction.id","transaction.status","transaction.amount_in_cents"],
                    "checksum":""
                  }
                }
                """.formatted(timestamp, TRANSACTION_ID));
        String checksum = calcularChecksum(evento);
        ((ObjectNode) evento.path("signature")).put("checksum", checksum);
        return evento;
    }

    private String calcularChecksum(JsonNode evento) throws Exception {
        StringBuilder cadena = new StringBuilder();
        JsonNode data = evento.path("data");
        for (JsonNode propiedad : evento.path("signature").path("properties")) {
            JsonNode valor = data;
            for (String segmento : propiedad.asText().split("\\.")) {
                valor = valor.path(segmento);
            }
            cadena.append(valor.asText());
        }
        cadena.append(evento.path("timestamp").asText()).append(EVENT_SECRET);
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(cadena.toString().getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
