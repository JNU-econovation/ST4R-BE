package star.member.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class AlreadyInvalidatedTokenException extends ClientException implements MemberException {
    public AlreadyInvalidatedTokenException() {
        super(ErrorCode.ALREADY_INVALIDATED_TOKEN);
    }
}
