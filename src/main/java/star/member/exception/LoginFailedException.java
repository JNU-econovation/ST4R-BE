package star.member.exception;

public class LoginFailedException extends RuntimeException {
    private static final String ERROR_MESSAGE = "로그인에 실패하였습니다.";

    public LoginFailedException() {
        super(ERROR_MESSAGE);
    }
}
