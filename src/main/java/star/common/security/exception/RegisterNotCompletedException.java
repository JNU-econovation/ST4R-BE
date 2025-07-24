package star.common.security.exception;

import star.common.exception.ErrorCode;

public class RegisterNotCompletedException extends CustomAccessDeniedException{

    public RegisterNotCompletedException() {
        super(ErrorCode.REGISTER_NOT_COMPLETED_ERROR);
    }

}
