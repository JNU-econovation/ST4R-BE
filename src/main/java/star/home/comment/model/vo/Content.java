package star.home.comment.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Content(
        String value
) {
    private static final int CONTENT_MAX_LENGTH = 300;
    private static final int CONTENT_MIN_LENGTH = 2;

    public Content {
        validate(value);
    }
    private void validate(String value) {
        if (value == null || value.length() < CONTENT_MIN_LENGTH
                || value.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "댓글의 길이는 최소 %d자, 최대 %d자여야 합니다.".formatted(CONTENT_MIN_LENGTH,
                            CONTENT_MAX_LENGTH));
        }
    }
}
