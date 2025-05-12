package star.home.board.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Content(
        @Column(nullable = false)
        String text,

        @Column(nullable = true)
        Jido map
) {

}
