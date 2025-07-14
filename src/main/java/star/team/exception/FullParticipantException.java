package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class FullParticipantException extends ClientException implements TeamException {
    public FullParticipantException() {
        super(ErrorCode.FULL_PARTICIPANT);
    }
}
