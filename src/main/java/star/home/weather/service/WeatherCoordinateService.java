package star.home.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.weather.client.WeatherClient;
import star.home.weather.dto.AddressDTO;
import star.home.weather.dto.client.response.WeatherClientResponse;
import star.home.weather.dto.request.WeatherRequest;
import star.home.weather.dto.response.WeatherResponse;
import star.home.weather.enums.WeatherEnum;

@Service
@RequiredArgsConstructor
public class WeatherCoordinateService {

    private final GeocodingService geocodingService;
    private final WeatherClient weatherClient;

    public WeatherResponse getCurrentWeather(WeatherRequest request) {
        AddressDTO address = geocodingService.getAddressFromGPS(request);

        WeatherClientResponse clientResponse = weatherClient.getCurrentWeather(request);

        Integer weatherId = clientResponse.weather().getFirst().id();

        return WeatherResponse.builder()
                .address(address)
                .temp(clientResponse.main().temp())
                .weather(WeatherEnum.fromCode(weatherId))
                .build();
    }
}
