package star.home.weather.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import star.common.exception.client.BadDataSyntaxException;

public record WeatherRequest(
        @NotNull(message = "위도를 입력해주세요")
        Double latitude,

        @NotNull(message = "경도를 입력해주세요")
        Double longitude
) {

    @JsonCreator
    public WeatherRequest {
        validate(latitude, longitude);
    }

    private void validate(Double latitude, Double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new BadDataSyntaxException("위도는 -90 ~ +90 이여야 합니다.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new BadDataSyntaxException("경도는  -180 ~ +180 이여야 합니다.");
        }
    }

}
