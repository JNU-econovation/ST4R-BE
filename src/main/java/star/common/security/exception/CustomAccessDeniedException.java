package star.common.security.exception;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;
import star.common.exception.ErrorCode;

@Getter
public class CustomAccessDeniedException extends AccessDeniedException {
    private final ErrorCode errorCode;

    public CustomAccessDeniedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return getErrorCode().getMessage();
    }
}
