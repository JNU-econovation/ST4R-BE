package star.member.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class AlreadyCompletedRegistrationException extends ClientException implements MemberException {

    public AlreadyCompletedRegistrationException() {
        super(ErrorCode.ALREADY_COMPLETED_REGISTRATION);
    }
}