package star.team.model.vo;

import static star.team.constants.TeamConstants.PARTICIPANT_MAX_CAPACITY;
import static star.team.constants.TeamConstants.PARTICIPANT_MIN_CAPACITY;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.team.exception.EmptyParticipantException;
import star.team.exception.FullParticipantException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {

    @Column(name = "participant_current")
    private Integer current;

    @Column(name = "participant_capacity")
    private Integer capacity;

    @Builder
    public Participant(Integer current, Integer capacity) {
        validate(current, capacity);
        this.current = current;
        this.capacity = capacity;
    }

    public void incrementCurrent() {
        if (this.current + 1 >= this.capacity) {
            throw new FullParticipantException();
        }
        this.current++;
    }

    public void decrementCurrent() {
        if (this.current - 1 < PARTICIPANT_MIN_CAPACITY) {
            throw new EmptyParticipantException();
        }
        this.current--;
    }

    public void setCapacity(Integer capacity) {
        validate(current, capacity);
        this.capacity = capacity;
    }

    //todo: 추후 business exception 으로 refactoring
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
