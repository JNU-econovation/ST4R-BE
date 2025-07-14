package star.common.auth.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class InvalidRedirectUriException extends ClientException implements AuthException {

    private final String origin;

    public InvalidRedirectUriException(String origin) {
        super(ErrorCode.INVALID_REDIRECT_URI);
        this.origin = origin;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), origin);
    }
}