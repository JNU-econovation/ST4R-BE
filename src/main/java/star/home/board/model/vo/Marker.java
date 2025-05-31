package star.home.board.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Marker(
        Double latitude,
        Double longitude,
        String locationName,
        String roadAddress
) {

    private static final int LOCATION_NAME_MAX_LENGTH = 100;
    private static final int ROAD_ADDRESS_MAX_LENGTH = 200;

    public Marker {
        validate(latitude, longitude, locationName, roadAddress);
    }

    public static Marker copyOf(Marker marker) {
        if (marker == null) {
            return null;
        }
        return new Marker(marker.latitude(), marker.longitude(), marker.locationName(),
                marker.roadAddress());
    }

    private void validate(Double latitude, Double longitude, String locationName,
            String roadAddress) {
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90 이상 90 이하의 값이어야 합니다.");
        }

        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180 이상 180 이하의 값이어야 합니다.");
        }

        if (locationName == null || locationName.length() > LOCATION_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "장소 이름은 최대 %d자까지 허용됩니다.".formatted(LOCATION_NAME_MAX_LENGTH));
        }

        if (roadAddress == null || roadAddress.length() > ROAD_ADDRESS_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "도로몀 주소는 최대 %d자까지 허용됩니다.".formatted(ROAD_ADDRESS_MAX_LENGTH));
        }
    }
}

