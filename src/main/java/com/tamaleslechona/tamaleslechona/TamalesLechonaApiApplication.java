package com.tamaleslechona.tamaleslechona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class TamalesLechonaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TamalesLechonaApiApplication.class, args);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}