package star.home.board.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Marker(
        Double latitude,
        Double longitude,
        String title
) {

}
