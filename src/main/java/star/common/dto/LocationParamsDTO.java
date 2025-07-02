package star.common.dto;

import org.springframework.lang.Nullable;
import star.common.model.vo.CircularArea;
import star.common.model.vo.Marker;

public record LocationParamsDTO(
        Double latitude,
        Double longitude,
        Double distanceInMeters,
        String roadAddress,
        @Nullable String locationName
) {

    public CircularArea toCircularArea() {
        if (latitude == null && longitude == null && distanceInMeters == null && roadAddress == null
                && locationName == null) {
            return null;
        }

        return CircularArea.builder()
                .marker(Marker.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .roadAddress(roadAddress)
                        .locationName(locationName)
                        .build())
                .distanceInMeters(distanceInMeters)
                .build();
    }
}