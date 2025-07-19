package star.home.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.weather.client.GeocodingClient;
import star.home.weather.dto.AddressDTO;
import star.home.weather.dto.client.response.ReverseGeocodingResponse;
import star.home.weather.dto.client.response.ReverseGeocodingResponse.StructureData;
import star.home.weather.dto.request.WeatherRequest;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final GeocodingClient client;

    public AddressDTO getAddressFromGPS(WeatherRequest request) {

        ReverseGeocodingResponse response = client.doReverseGeocoding(request);

        StructureData structure = response
                .response()
                .result()
                .getFirst()
                .structure();

        return new AddressDTO(structure.level1(), structure.level2());
    }
}

