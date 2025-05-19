package star.team.model.vo;

import static star.team.constants.TeamConstants.PASSWORD_MAX_LENGTH;
import static star.team.constants.TeamConstants.PASSWORD_MIN_LENGTH;

import jakarta.persistence.Embeddable;

@Embeddable
public record PlainPassword(
        String value
) {
    public PlainPassword {
        validate(value);
    }

    private void validate(String value) {
        if (value == null) {
            return;
        }
        if (value.length() < PASSWORD_MIN_LENGTH
                || value.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "비밀번호는 최소 %d자리, 최대 %d자리만 가능합니다.".formatted(PASSWORD_MIN_LENGTH,
                            PASSWORD_MAX_LENGTH));
        }
    }
}
