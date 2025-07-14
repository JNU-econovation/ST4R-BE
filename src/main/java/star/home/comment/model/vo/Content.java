package star.home.comment.model.vo;

import static star.home.comment.constants.CommentConstants.COMMENT_CONTENT_MAX_LENGTH;
import static star.home.comment.constants.CommentConstants.COMMENT_CONTENT_MIN_LENGTH;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.exception.client.BadDataLengthException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    private String value;

    @JsonCreator
    public Content(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.length() < COMMENT_CONTENT_MIN_LENGTH
                || value.length() > COMMENT_CONTENT_MAX_LENGTH) {

            throw new BadDataLengthException(
                    "댓글", COMMENT_CONTENT_MIN_LENGTH, COMMENT_CONTENT_MAX_LENGTH
            );
        }
    }
}
