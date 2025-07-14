package star.common.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import star.common.exception.client.BadDataMeaningException;
import star.common.exception.client.BadDataSyntaxException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Slf4j
public class Marker {
    private Double latitude;
    private Double longitude;
    private String roadAddress;

    @Nullable
    private String locationName;

    private static final int LOCATION_NAME_MAX_LENGTH = 100;
    private static final int ROAD_ADDRESS_MAX_LENGTH = 200;

    @JsonCreator
    public Marker (Double latitude, Double longitude, String roadAddress, String locationName) {
        validate(latitude, longitude, locationName, roadAddress);
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.roadAddress = roadAddress;
    }

    public static void validate(Double latitude, Double longitude, String locationName,
            String roadAddress) {
        if (latitude == null) {
            throw new BadDataSyntaxException("위도(latitude)를 입력해주세요.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new BadDataMeaningException("위도는 -90도 이상 90도 이하로 입력해주세요.");
        }

        if (longitude == null) {
            throw new BadDataSyntaxException("경도(longitude)를 입력해주세요.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new BadDataMeaningException("경도는 -180도 이상 180도 이하로 입력해주세요.");
        }
        if (locationName != null && locationName.length() > LOCATION_NAME_MAX_LENGTH) {
            throw new BadDataMeaningException(
                    "장소 이름은 최대 %d자까지 입력할 수 있습니다.".formatted(LOCATION_NAME_MAX_LENGTH));
        }

        if (roadAddress == null || roadAddress.isBlank()) {
            throw new BadDataSyntaxException("도로명 주소를 입력해주세요.");
        }
        if (roadAddress.length() > ROAD_ADDRESS_MAX_LENGTH) {
            throw new BadDataMeaningException(
                    "도로명 주소는 최대 %d자까지 입력할 수 있습니다.".formatted(ROAD_ADDRESS_MAX_LENGTH));
        }
    }
}

