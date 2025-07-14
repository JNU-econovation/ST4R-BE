package star.common.exception.server;


import star.common.exception.BusinessException;
import star.common.exception.ErrorCode;

public class InternalServerException extends BusinessException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
