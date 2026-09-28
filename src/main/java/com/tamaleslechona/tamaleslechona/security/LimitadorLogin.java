package com.tamaleslechona.tamaleslechona.security;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.tamaleslechona.tamaleslechona.exception.DemasiadosIntentosException;

// Limita los intentos fallidos de login. Cuenta por dos llaves a la vez:
//   - la IP de quien intenta (frena a un atacante que prueba muchos correos)
//   - el correo que se intenta (frena a un atacante que cambia de IP)
// Con MAX_FALLOS fallos dentro de la VENTANA, esa llave queda bloqueada
// hasta que la ventana termine. Todo vive en memoria: si el servicio se
// reinicia, los contadores vuelven a cero.
@Component
public class LimitadorLogin {

    private static final int MAX_FALLOS = 5;
    private static final Duration VENTANA = Duration.ofMinutes(15);
    private static final int LIMITE_PARA_LIMPIAR = 10_000;

    // fallos acumulados y el instante del primer fallo (inicio de la ventana)
    private record Registro(int fallos, Instant inicio) {}

    private final ConcurrentHashMap<String, Registro> registros = new ConcurrentHashMap<>();

    // Se llama ANTES de validar la contraseña: si la IP o el correo estan
    // bloqueados, corta aqui sin siquiera consultar la base de datos.
    public void verificarNoBloqueado(String ip, String correo) {
        Instant ahora = Instant.now();
        for (String clave : claves(ip, correo)) {
            Registro r = registros.get(clave);
            if (r == null) continue;

            Instant fin = r.inicio().plus(VENTANA);
            if (ahora.isAfter(fin)) {
                registros.remove(clave, r); // la ventana ya vencio
                continue;
            }
            if (r.fallos() >= MAX_FALLOS) {
                long minutos = Math.max(1, Duration.between(ahora, fin).toMinutes() + 1);
                throw new DemasiadosIntentosException(
                        "Demasiados intentos fallidos. Intenta de nuevo en " + minutos + " minuto(s).");
            }
        }
    }

    // Se llama cuando las credenciales fueron incorrectas.
    public void registrarFallo(String ip, String correo) {
        Instant ahora = Instant.now();
        limpiarSiHaceFalta(ahora);
        for (String clave : claves(ip, correo)) {
            // merge es atomico: seguro si llegan varias peticiones a la vez
            registros.merge(clave, new Registro(1, ahora), (viejo, nuevo) ->
                    ahora.isAfter(viejo.inicio().plus(VENTANA))
                            ? nuevo
                            : new Registro(viejo.fallos() + 1, viejo.inicio()));
        }
    }

    // Login correcto: solo se limpia el contador del CORREO. El de la IP se
    // deja vencer solo; si no, un atacante con una cuenta propia podria
    // reiniciar su contador entrando bien entre intento e intento.
    public void registrarExito(String correo) {
        registros.remove("correo:" + normalizar(correo));
    }

    private List<String> claves(String ip, String correo) {
        return List.of("ip:" + ip, "correo:" + normalizar(correo));
    }

    private String normalizar(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase();
    }

    // Evita que el mapa crezca sin limite si alguien inunda con correos falsos.
    private void limpiarSiHaceFalta(Instant ahora) {
        if (registros.size() > LIMITE_PARA_LIMPIAR) {
            registros.entrySet().removeIf(e -> ahora.isAfter(e.getValue().inicio().plus(VENTANA)));
        }
    }
}