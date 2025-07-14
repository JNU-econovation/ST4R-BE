package star.common.exception.client;

import star.common.exception.BusinessException;
import star.common.exception.ErrorCode;


public abstract class ClientException extends BusinessException {

    public ClientException(ErrorCode errorCode) {
        super(errorCode);
    }
}
