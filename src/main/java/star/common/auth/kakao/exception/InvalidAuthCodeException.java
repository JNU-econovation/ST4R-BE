package star.common.auth.kakao.exception;

import star.common.auth.exception.AuthException;
import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class InvalidAuthCodeException extends ClientException implements AuthException {
    public InvalidAuthCodeException() {
        super(ErrorCode.INVALID_AUTH_CODE);
    }
}
