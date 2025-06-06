package star.common.model.vo;

import jakarta.persistence.Embeddable;
import star.home.board.model.vo.Marker;

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

    public static Jido copyOf(Jido jido) {
        if (jido == null) return null;
        return new Jido(Marker.copyOf(jido.marker()), jido.zoomLevel());
    }

    private void validate(Integer zoomLevel) {
        if (zoomLevel == null || zoomLevel < MIN_ZOOM_LEVEL || zoomLevel > MAX_ZOOM_LEVEL) {
            throw new IllegalArgumentException(
                    "줌 레벨은 %d 이상 %d 이하의 값이어야 합니다.".formatted(MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL));
        }
    }
}
