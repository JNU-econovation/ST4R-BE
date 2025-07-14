package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class TargetIsNotBannedException extends ClientException implements TeamException {
    public TargetIsNotBannedException() {
        super(ErrorCode.TARGET_IS_NOT_BANNED);
    }
}
