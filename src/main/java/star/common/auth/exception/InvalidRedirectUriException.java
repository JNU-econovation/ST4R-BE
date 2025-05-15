package star.common.auth.exception;

import star.common.exception.ClientException;

public class InvalidRedirectUriException extends ClientException {

    public InvalidRedirectUriException(String message) {
        super(message);
    }
}
