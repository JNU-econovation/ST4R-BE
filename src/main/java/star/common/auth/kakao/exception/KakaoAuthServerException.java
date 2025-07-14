package star.common.auth.kakao.exception;

import star.common.auth.exception.AuthException;
import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;

public class KakaoAuthServerException extends InternalServerException implements AuthException {
    public KakaoAuthServerException() {
        super(ErrorCode.KAKAO_AUTH_SERVER_ERROR);
    }
}
