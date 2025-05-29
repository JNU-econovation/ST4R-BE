package star.team.exception;

import star.common.exception.client.ClientException;

public class EmptyParticipantException extends ClientException {
    private static final String ERROR_MESSAGE = "참여자가 0 입니다.";

    public EmptyParticipantException() {
        super(ERROR_MESSAGE);
    }
}
