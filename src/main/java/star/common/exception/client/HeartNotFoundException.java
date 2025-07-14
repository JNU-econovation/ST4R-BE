package star.common.exception.client;

import star.common.exception.ErrorCode;

public class HeartNotFoundException extends ClientException {
    public HeartNotFoundException() {
        super(ErrorCode.HEART_NOT_FOUND);
    }
}
