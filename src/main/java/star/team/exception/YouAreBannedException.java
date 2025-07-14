package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class YouAreBannedException extends ClientException implements TeamException {
    public YouAreBannedException() {
        super(ErrorCode.YOU_ARE_BANNED);
    }
}
