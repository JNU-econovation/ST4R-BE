package star.common.security.exception;

import org.springframework.security.core.AuthenticationException;
import star.common.exception.ErrorCode;

public class CustomAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;


    public CustomAuthenticationException() {
        super(ErrorCode.UNAUTHORIZED_ERROR.getMessage());
        this.errorCode = ErrorCode.UNAUTHORIZED_ERROR;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
