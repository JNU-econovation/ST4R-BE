package star.member.exception;

import star.common.exception.ClientException;

public class AlreadyInvalidatedTokenException extends ClientException {
    private static final String ERROR_MESSAGE = "이미 카카오 토큰이 만료되었습니다.";

    public AlreadyInvalidatedTokenException() {
        super(ERROR_MESSAGE);
    }
}
