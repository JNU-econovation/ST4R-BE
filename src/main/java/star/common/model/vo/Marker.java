package star.common.model.vo;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Marker {
    private Double latitude;
    private Double longitude;
    private String roadAddress;

    @Nullable
    private String locationName;

    private static final int LOCATION_NAME_MAX_LENGTH = 100;
    private static final int ROAD_ADDRESS_MAX_LENGTH = 200;

    

    public static void validate(Double latitude, Double longitude, String locationName,
            String roadAddress) {
        if (latitude == null) {
            throw new IllegalArgumentException("위도(latitude)를 입력해주세요.");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90도 이상 90도 이하로 입력해주세요.");
        }

        if (longitude == null) {
            throw new IllegalArgumentException("경도(longitude)를 입력해주세요.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180도 이상 180도 이하로 입력해주세요.");
        }
        if (locationName != null && locationName.length() > LOCATION_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "장소 이름은 최대 %d자까지 입력할 수 있습니다.".formatted(LOCATION_NAME_MAX_LENGTH));
        }

        if (roadAddress == null || roadAddress.isBlank()) {
            throw new IllegalArgumentException("도로명 주소를 입력해주세요.");
        }
        if (roadAddress.length() > ROAD_ADDRESS_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "도로명 주소는 최대 %d자까지 입력할 수 있습니다.".formatted(ROAD_ADDRESS_MAX_LENGTH));
        }
    }
}

