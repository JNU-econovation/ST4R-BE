package star.team.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Participant (
        @Column(name = "participant_current")
        Integer current,

        @Column(name = "participant_capacity")
        Integer capacity

) {
    private final static Integer PARTICIPANT_MIN_CAPACITY = 1;
    private final static Integer PARTICIPANT_MAX_CAPACITY = 30;

    public Participant {
        validate(current, capacity);
    }

    private void validate(Integer current, Integer capacity) {
        if (current == null) {
            throw new IllegalArgumentException("참여자 수를 입력해주세요.");
        }

        if (capacity == null) {
            throw new IllegalArgumentException("정원 수를 입력해주세요.");
        }

        if (current < PARTICIPANT_MIN_CAPACITY) {
            throw new IllegalArgumentException(
                    "참여자 수는 %d보다 작을 수 없습니다.".formatted(PARTICIPANT_MIN_CAPACITY));
        }

        if (capacity > PARTICIPANT_MAX_CAPACITY) {
            throw new IllegalArgumentException(
                    "정원 수는 %d보다 적어야 합니다.".formatted(PARTICIPANT_MAX_CAPACITY));
        }

        if (current >= capacity) {
            throw new IllegalArgumentException("그룹이 꽉 찼습니다.");
        }
    }
}
