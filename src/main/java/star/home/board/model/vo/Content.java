package star.home.board.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.vo.Jido;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Content {
    @Column(nullable = false)
    private String text;

    @Column(nullable = true)
    private Jido map;

    private static final int CONTENT_MAX_LENGTH = 5000;
    private static final int CONTENT_MIN_LENGTH = 10;

    

    private void validate(String text) {
        if (text == null || text.length() < CONTENT_MIN_LENGTH
                || text.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "내용의 길이는 최소 %d자, 최대 %d자여야 합니다.".formatted(CONTENT_MIN_LENGTH,
                            CONTENT_MAX_LENGTH));
        }
    }
}
