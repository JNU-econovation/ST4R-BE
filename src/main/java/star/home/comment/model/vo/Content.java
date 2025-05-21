package star.home.comment.model.vo;

import static star.home.comment.constants.CommentConstants.COMMENT_CONTENT_MAX_LENGTH;
import static star.home.comment.constants.CommentConstants.COMMENT_CONTENT_MIN_LENGTH;

import jakarta.persistence.Embeddable;

@Embeddable
public record Content(
        String value
) {


    public Content {
        validate(value);
    }
    private void validate(String value) {
        if (value == null || value.length() < COMMENT_CONTENT_MIN_LENGTH
                || value.length() > COMMENT_CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "댓글의 길이는 최소 %d자, 최대 %d자여야 합니다.".formatted(COMMENT_CONTENT_MIN_LENGTH,
                            COMMENT_CONTENT_MAX_LENGTH));
        }
    }
}
