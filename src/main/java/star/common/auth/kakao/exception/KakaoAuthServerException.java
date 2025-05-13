package star.common.auth.kakao.exception;

public class KakaoAuthServerException extends RuntimeException {
    private final static String ERROR_MESSAGE = "카카오 서버 에러 발생";
    public KakaoAuthServerException() {
        super(ERROR_MESSAGE);
    }
}
