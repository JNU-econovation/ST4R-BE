package star.home.board.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Jido(
        Marker marker,
        Integer zoomLevel
) {

}
