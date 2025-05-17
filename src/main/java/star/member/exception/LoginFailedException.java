package star.member.exception;

import star.common.exception.server.InternalServerException;

public class LoginFailedException extends InternalServerException {
    private static final String ERROR_MESSAGE = "로그인에 실패하였습니다.";

    public LoginFailedException() {
        super(ERROR_MESSAGE);
    }
}
