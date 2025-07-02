package star.home.board.model.vo;

import static star.home.constants.HomeConstants.TITLE_MAX_LENGTH;
import static star.home.constants.HomeConstants.TITLE_MIN_LENGTH;

import jakarta.persistence.Embeddable;
import star.common.exception.client.BadDataLengthException;

@Embeddable
public record Title(
        String value
) {

    public Title {
        validateTitle(value);
    }

    private void validateTitle(String value) {
        if (value == null || value.length() < TITLE_MIN_LENGTH
                || value.length() > TITLE_MAX_LENGTH) {
            throw new BadDataLengthException("제목", TITLE_MIN_LENGTH, TITLE_MAX_LENGTH);
        }
    }
}
