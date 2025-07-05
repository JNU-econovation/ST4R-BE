package star.home.board.model.vo;

import static star.home.board.constants.BoardConstants.CONTENT_MAX_LENGTH;
import static star.home.board.constants.BoardConstants.CONTENT_MIN_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.exception.client.BadDataLengthException;
import star.common.model.vo.Jido;

@Embeddable
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Content {
    @Column(nullable = false)
    private String text;

    @Column(nullable = true)
    private Jido map;


    private void validate(String text) {
        if (text == null || text.length() < CONTENT_MIN_LENGTH
                || text.length() > CONTENT_MAX_LENGTH) {
            throw new BadDataLengthException("내용", CONTENT_MIN_LENGTH, CONTENT_MAX_LENGTH);
        }
    }
}
