package star.home.weather.dto.client.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WeatherClientResponse(
        List<Weather> weather,
        String base,
        Main main,
        Integer cod
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Weather(Integer id, String main, String description) { }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Main(Double temp) { }

}