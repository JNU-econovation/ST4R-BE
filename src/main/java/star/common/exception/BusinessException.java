package star.common.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public abstract class BusinessException extends NestedRuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
