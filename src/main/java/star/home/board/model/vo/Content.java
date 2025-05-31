package star.home.board.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import star.common.model.vo.Jido;

@Embeddable
public record Content(
        @Column(nullable = false)
        String text,

        @Column(nullable = true)
        Jido map
) {
    private static final int CONTENT_MAX_LENGTH = 5000;
    private static final int CONTENT_MIN_LENGTH = 10;

    public Content {
        validate(text);
    }

    public static Content copyOf(Content content) {
        if (content == null) return null;
        return new Content(content.text(), Jido.copyOf(content.map()));
    }

    private void validate(String text) {
        if (text == null || text.length() < CONTENT_MIN_LENGTH
                || text.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "내용의 길이는 최소 %d자, 최대 %d자여야 합니다.".formatted(CONTENT_MIN_LENGTH,
                            CONTENT_MAX_LENGTH));
        }
    }
}
