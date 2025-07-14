package star.home.board.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class AlreadyCanceledHeartException extends ClientException implements BoardException {
    public AlreadyCanceledHeartException() {
        super(ErrorCode.ALREADY_CANCELED_HEART);
    }
}
