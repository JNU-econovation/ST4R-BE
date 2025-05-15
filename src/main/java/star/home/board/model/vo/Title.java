package star.home.board.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

@Embeddable
public record Title(
        String value
) {

    private static final int TITLE_MIN_LENGTH = 2;
    private static final int TITLE_MAX_LENGTH = 30;


    public Title {
        if (value == null || value.length() < TITLE_MIN_LENGTH || value.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "제목의 길이는 최소 %d자, 최대 %d자여야 합니다.".formatted(TITLE_MIN_LENGTH, TITLE_MAX_LENGTH));
        }
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Title fromString(String value) {
        return new Title(value);
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}