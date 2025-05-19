package star.team.model.vo;

import static star.team.constants.TeamConstants.DESCRIPTION_MAX_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Description(
        @Column(name = "description", nullable = true)
        String value
) {
    public Description {
        validate(value);
    }

    private void validate(String value) {
        if (value == null) {
            return;
        }

        if (value.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "설명은 최대 %d자 까지 가능합니다.".formatted(DESCRIPTION_MAX_LENGTH));
        }

    }
}