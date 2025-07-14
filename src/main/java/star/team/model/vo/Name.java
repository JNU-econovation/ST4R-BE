package star.team.model.vo;

import static star.team.constants.TeamConstants.NAME_MAX_LENGTH;
import static star.team.constants.TeamConstants.NAME_MIN_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.exception.client.BadDataLengthException;
import star.common.exception.client.BadDataSyntaxException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {
    @Column(name = "name", nullable = false)
    private String value;

    public Name(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null) {
            throw new BadDataSyntaxException("모임 이름을 입력해주세요.");
        }

        if (value.length() > NAME_MAX_LENGTH || value.length() < NAME_MIN_LENGTH) {
            throw new BadDataLengthException("모임 제목", NAME_MIN_LENGTH, NAME_MAX_LENGTH);
        }
    }
}
