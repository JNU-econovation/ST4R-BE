package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class NewPasswordSameAsOldException extends ClientException implements TeamException {

    public NewPasswordSameAsOldException() {
        super(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
    }
}