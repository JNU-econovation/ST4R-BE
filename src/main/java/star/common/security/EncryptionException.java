package star.common.security;

import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;

public class EncryptionException extends InternalServerException {

    public EncryptionException() {
        super(ErrorCode.ENCRYPTION_ERROR);
    }
}