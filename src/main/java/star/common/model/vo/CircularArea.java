package star.common.model.vo;

import lombok.Builder;

@Builder
public record CircularArea(
        Marker marker,
        Double distanceInMeters
) {
    public CircularArea {
        validate(marker, distanceInMeters);
    }


    private void validate(Marker marker, Double distanceInMeters) {
        if (marker == null || distanceInMeters == null) {
            throw new IllegalArgumentException("위도 / 경도 또는 distanceInMeters를 입력해주세요");
        }
    }

}