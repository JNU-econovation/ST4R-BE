package star.common.auth.kakao.exception;

import star.common.exception.InternalServerException;

public class KakaoAuthServerException extends InternalServerException {
    private final static String ERROR_MESSAGE = "카카오 서버 에러 발생";
    public KakaoAuthServerException() {
        super(ERROR_MESSAGE);
    }
}
