package star.home.board.model.vo;

import static star.home.board.constants.BoardConstants.CONTENT_MAX_LENGTH;
import static star.home.board.constants.BoardConstants.CONTENT_MIN_LENGTH;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.exception.client.BadDataLengthException;
import star.common.model.vo.Jido;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Content {

    @Column(nullable = false)
    private String text;

    @Column(nullable = true)
    private Jido map;

    @JsonCreator
    public Content(String text, Jido map) {
        validate(text);
        this.text = text;
        this.map = map;
    }

    private void validate(String text) {
        if (text == null || text.length() < CONTENT_MIN_LENGTH
                || text.length() > CONTENT_MAX_LENGTH) {
            throw new BadDataLengthException("내용", CONTENT_MIN_LENGTH, CONTENT_MAX_LENGTH);
        }
    }
}
