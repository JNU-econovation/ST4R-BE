package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class EmptyParticipantException extends ClientException implements TeamException {

    public EmptyParticipantException() {
        super(ErrorCode.EMPTY_PARTICIPANT);
    }
}
