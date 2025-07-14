package star.common.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import star.common.exception.client.BadDataSyntaxException;

@Builder
public record CircularArea(
        Marker marker,
        Double distanceInMeters
) {

    @JsonCreator
    public CircularArea {
        validate(marker, distanceInMeters);
    }


    private void validate(Marker marker, Double distanceInMeters) {
        if (marker == null || distanceInMeters == null) {
            throw new BadDataSyntaxException("위도 / 경도 또는 distanceInMeters를 입력해주세요");
        }
    }

}