package star.team.model.vo;

import static star.team.constants.TeamConstants.NAME_MAX_LENGTH;
import static star.team.constants.TeamConstants.NAME_MIN_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Name {
    @Column(name = "name", nullable = false)
    private String value;

    

    private void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("그룹 이름을 입력해주세요.");
        }

        if (value.length() > NAME_MAX_LENGTH || value.length() < NAME_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "그룹의 제목은 최소 %d자, 최대 %d자여야 합니다.".formatted(NAME_MIN_LENGTH, NAME_MAX_LENGTH));
        }
    }
}
