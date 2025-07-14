package star.common.exception.client;

import star.common.exception.ErrorCode;

public class BadDataSyntaxException extends ClientException {

    private final String errorMessage;

    public BadDataSyntaxException(String message) {
        super(ErrorCode.BAD_DATA_SYNTAX);
        this.errorMessage = message;
    }

    @Override
    public String getMessage() {
        return String.format(
                getErrorCode().getMessage(), errorMessage);
    }
}
