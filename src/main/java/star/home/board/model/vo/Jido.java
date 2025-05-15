package star.home.board.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Jido(
        Marker marker,
        Integer zoomLevel
) {
    private static final int MIN_ZOOM_LEVEL = 1;
    private static final int MAX_ZOOM_LEVEL = 14;

    public Jido {
        validate(zoomLevel);
    }

    private void validate(Integer zoomLevel) {
        if (zoomLevel == null || zoomLevel < MIN_ZOOM_LEVEL || zoomLevel > MAX_ZOOM_LEVEL) {
            throw new IllegalArgumentException(
                    "줌 레벨은 %d 이상 %d 이하의 값이어야 합니다.".formatted(MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL));
        }
    }
}
