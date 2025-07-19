package star.home.weather.client;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import star.home.weather.config.WeatherRequestConfig;
import star.home.weather.dto.client.response.WeatherClientResponse;
import star.home.weather.dto.request.WeatherRequest;

@Component
@RequiredArgsConstructor
public class WeatherClient {

    private final WeatherRequestConfig config;
    private final RestClient client = RestClient.builder().build();

    public WeatherClientResponse getCurrentWeather(WeatherRequest request) {
        String baseUrl = config.currentBaseUrl();

        String uriString = UriComponentsBuilder.newInstance()
                .uri(URI.create(baseUrl))
                .queryParam("lat", request.latitude())
                .queryParam("lon", request.longitude())
                .queryParam("appid", config.restApiKey())
                .queryParam("units", "metric")
                .queryParam("mode", "JSON")
                .queryParam("lang", "kr")
                .build(true)
                .toUriString();

        return client.get()
                .uri(URI.create(uriString))
                .retrieve()
                .toEntity(WeatherClientResponse.class)
                .getBody();
    }
}
