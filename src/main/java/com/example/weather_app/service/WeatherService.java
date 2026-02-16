package com.example.weather_app.service;

import com.example.weather_app.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeather(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appId=" + apiKey + "&units=metric";
        try {
            return restTemplate.getForObject(url, WeatherResponse.class);
        } catch (HttpClientErrorException e) {
            return null;
        } catch (Exception e) {
            // Log the exception (in a real app) usually
            return null;
        }
    }

    public WeatherResponse getWeatherByCoordinates(double lat, double lon) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appId=" + apiKey
                + "&units=metric";
        try {
            return restTemplate.getForObject(url, WeatherResponse.class);
        } catch (HttpClientErrorException e) {
            return null;
        } catch (Exception e) {
            // Log the exception
            return null;
        }
    }
}
