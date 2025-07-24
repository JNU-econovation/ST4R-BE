package star.common.infra.aws.exception;

import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;

public class S3UnknownException extends InternalServerException {

    public S3UnknownException() {
        super(ErrorCode.S3_UNKNOWN_ERROR);
    }

}
