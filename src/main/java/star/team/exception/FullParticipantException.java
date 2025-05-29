package star.team.exception;

import star.common.exception.client.Client409Exception;

public class FullParticipantException extends Client409Exception {
    private static final String ERROR_MESSAGE = "참여자 수가 꽉 찼습니다.";

    public FullParticipantException() {
        super(ERROR_MESSAGE);
    }
}
