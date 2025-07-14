package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class CanNotBanSelfException extends ClientException implements TeamException {
    public CanNotBanSelfException() {
        super(ErrorCode.CANNOT_BAN_SELF);
    }
}
