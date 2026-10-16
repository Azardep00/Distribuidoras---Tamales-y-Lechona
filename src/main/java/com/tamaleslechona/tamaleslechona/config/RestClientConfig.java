package com.tamaleslechona.tamaleslechona.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${microservicio.clima.url}")
    private String climaUrl;

    // Proveedor de respaldo (MET Norway): gratuito, sin API key. Exige un User-Agent
    // que identifique la aplicacion.
    @Value("${microservicio.clima.respaldo.url:https://api.met.no}")
    private String climaRespaldoUrl;

    // Bean con nombre propio: asi no choca con otros RestClient (p.ej. notificaciones).
    // Timeouts cortos: sin ellos, si el servicio externo se cuelga, la peticion
    // se queda esperando y el usuario ve un error generico.
    @Bean("climaRestClient")
    public RestClient climaRestClient() {
        return crear(climaUrl);
    }

    @Bean("climaRespaldoRestClient")
    public RestClient climaRespaldoRestClient() {
        return crear(climaRespaldoUrl);
    }

    private RestClient crear(String baseUrl) {
        HttpClient http = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(4))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(6));
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("User-Agent", "TamalesLechona/1.0 github.com/Azardep00/Distribuidoras---Tamales-y-Lechona")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
