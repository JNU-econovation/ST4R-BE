package star.common.exception.client;

import star.common.exception.ErrorCode;

public class AlreadyHeartedException extends ClientException {
    public AlreadyHeartedException() {
        super(ErrorCode.ALREADY_HEARTED);
    }
}
