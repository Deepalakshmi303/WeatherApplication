package com.example.weather_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.weather_app.model.WeatherResponse;
import com.example.weather_app.service.WeatherService;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lon", required = false) Double lon,
            Model model) {
        WeatherResponse weatherResponse = null;

        if (lat != null && lon != null) {
            weatherResponse = weatherService.getWeatherByCoordinates(lat, lon);
        } else if (city != null && !city.isEmpty()) {
            weatherResponse = weatherService.getWeather(city);
        }

        if (weatherResponse != null) {
            model.addAttribute("city", weatherResponse.getName());
            model.addAttribute("country", weatherResponse.getSys().getCountry());
            model.addAttribute("weatherDescription", weatherResponse.getWeather().get(0).getDescription());
            model.addAttribute("temperature", weatherResponse.getMain().getTemp());
            model.addAttribute("humidity", weatherResponse.getMain().getHumidity());
            model.addAttribute("windSpeed", weatherResponse.getWind().getSpeed());

            String weatherIcon = "wi wi-owm-" + weatherResponse.getWeather().get(0).getId();
            model.addAttribute("weatherIcon", weatherIcon);

            // Dynamic Weather Group
            String mainWeather = weatherResponse.getWeather().get(0).getMain().toLowerCase();
            String weatherGroup = "default";
            if (mainWeather.contains("clear")) {
                weatherGroup = "clear";
            } else if (mainWeather.contains("rain") || mainWeather.contains("drizzle")) {
                weatherGroup = "rain";
            } else if (mainWeather.contains("storm") || mainWeather.contains("thunderstorm")) {
                weatherGroup = "storm";
            } else if (mainWeather.contains("cloud")) {
                weatherGroup = "clouds";
            } else if (mainWeather.contains("snow")) {
                weatherGroup = "snow";
            }
            model.addAttribute("weatherGroup", weatherGroup);

            // Date and Time Calculation
            java.time.Instant instant = java.time.Instant.ofEpochSecond(weatherResponse.getDt());
            java.time.ZoneOffset zoneOffset = java.time.ZoneOffset.ofTotalSeconds(weatherResponse.getTimezone());
            java.time.ZonedDateTime zonedDateTime = java.time.ZonedDateTime.ofInstant(instant, zoneOffset);

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("EEEE, MMMM d, yyyy h:mm a");
            String formattedDate = zonedDateTime.format(formatter);

            model.addAttribute("localDate", formattedDate);
        } else {
            model.addAttribute("error", "City not found.");
        }

        return "weather";
    }
}
