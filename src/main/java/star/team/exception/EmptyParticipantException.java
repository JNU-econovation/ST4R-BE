package star.team.exception;

import static star.team.constants.TeamConstants.PARTICIPANT_MIN_CAPACITY;

import star.common.exception.client.Client409Exception;

public class EmptyParticipantException extends Client409Exception {

    private static final String ERROR_MESSAGE = "참여자는 최소 %d명 이여야 입니다.".formatted(
            PARTICIPANT_MIN_CAPACITY);

    public EmptyParticipantException() {
        super(ERROR_MESSAGE);
    }
}
