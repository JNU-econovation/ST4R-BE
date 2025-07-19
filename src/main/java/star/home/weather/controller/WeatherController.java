package star.home.weather.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.home.weather.dto.request.WeatherRequest;
import star.home.weather.dto.response.WeatherResponse;
import star.home.weather.service.WeatherCoordinateService;

@RestController
@RequestMapping("/home/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherCoordinateService service;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@Valid WeatherRequest request) {
        return ResponseEntity.ok(service.getCurrentWeather(request));
    }

}
