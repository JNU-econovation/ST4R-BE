package star.common.auth.kakao.exception;

import star.common.exception.server.InternalServerException;

public class InvalidAuthCodeException extends InternalServerException {
    private final static String ERROR_MESSAGE = "인가코드가 올바르지 않습니다.";
    public InvalidAuthCodeException() {
        super(ERROR_MESSAGE);
    }
}
