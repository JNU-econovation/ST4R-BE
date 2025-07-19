package star.home.weather.dto.response;

import lombok.Builder;
import star.home.weather.dto.AddressDTO;
import star.home.weather.enums.WeatherEnum;

@Builder
public record WeatherResponse(
    AddressDTO address,
    Double temp,
    WeatherEnum weather
) { }
