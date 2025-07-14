package star.common.auth.kakao.exception;

import star.common.auth.exception.AuthException;
import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;

public class KakaoAuthUnknownException extends InternalServerException implements AuthException {
    public KakaoAuthUnknownException() {
        super(ErrorCode.KAKAO_AUTH_UNKNOWN_ERROR);
    }
}