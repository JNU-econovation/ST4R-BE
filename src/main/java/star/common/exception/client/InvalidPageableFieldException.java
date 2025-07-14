package star.common.exception.client;

import star.common.exception.ErrorCode;

public class InvalidPageableFieldException extends ClientException {

    private final String fieldName;
    private final String clientInput;

    public InvalidPageableFieldException(String fieldName, String clientInput) {
        super(ErrorCode.INVALID_PAGEABLE_FIELD);
        this.fieldName = fieldName;
        this.clientInput = clientInput;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), fieldName, clientInput);
    }
}
