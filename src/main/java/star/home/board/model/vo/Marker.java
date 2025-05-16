package star.home.board.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Marker(
        Double latitude,
        Double longitude,
        String markerTitle
) {
    private static final int MARKER_TITLE_MAX_LENGTH = 100;

    public Marker {
        validate(latitude, longitude, markerTitle);
    }

    public static Marker copyOf(Marker marker) {
        if (marker == null) return null;
        return new Marker(marker.latitude(), marker.longitude(), marker.markerTitle());
    }

    private void validate(Double latitude, Double longitude, String markerTitle) {
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90 이상 90 이하의 값이어야 합니다.");
        }

        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180 이상 180 이하의 값이어야 합니다.");
        }

        if (markerTitle == null || markerTitle.length() > MARKER_TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("마커 제목은 최대 100자까지 허용됩니다.");
        }
    }
}

