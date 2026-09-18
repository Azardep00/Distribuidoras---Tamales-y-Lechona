package com.tamaleslechona.tamaleslechona.dto;

public record ClimaResponse(double latitude, double longitude, CurrentWeather current_weather) {
    public record CurrentWeather(double temperature, double windspeed, int weathercode) {}
}