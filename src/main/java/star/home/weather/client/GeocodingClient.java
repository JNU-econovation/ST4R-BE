package star.home.weather.client;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import star.home.weather.config.GeocodingRequestConfig;
import star.home.weather.dto.client.response.ReverseGeocodingResponse;
import star.home.weather.dto.request.WeatherRequest;

@Component
@RequiredArgsConstructor
public class GeocodingClient {

    private final GeocodingRequestConfig config;
    private final RestClient client = RestClient.builder().build();

    public ReverseGeocodingResponse doReverseGeocoding(WeatherRequest request) {

        String baseUrl = config.requestBaseUrl();

        var uri = UriComponentsBuilder
                .fromUri(URI.create(baseUrl))
                .queryParam("service", "address")
                .queryParam("request", "GetAddress")
                .queryParam("key", config.restApiKey())
                .queryParam("errorFormat", "json")
                .queryParam("point", request.longitude() + "," + request.latitude())
                .queryParam("type", "PARCEL")
                .queryParam("zipcode", false)
                .queryParam("simple", false)
                .queryParam("crs", "EPSG:4326")
                .build()
                .toUri();

        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(ReverseGeocodingResponse.class)
                .getBody();

    }
}
