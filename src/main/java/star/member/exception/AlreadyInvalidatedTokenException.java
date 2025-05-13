package star.member.exception;

public class AlreadyInvalidatedTokenException extends RuntimeException {
    private static final String ERROR_MESSAGE = "이미 카카오 토큰이 만료되었습니다.";

    public AlreadyInvalidatedTokenException() {
        super(ERROR_MESSAGE);
    }
}
