package star.common.exception.client;

import star.common.exception.ErrorCode;

public class YouAreNotAuthorException extends ClientException {
    public YouAreNotAuthorException() {
        super(ErrorCode.YOU_ARE_NOT_AUTHOR);
    }
}
