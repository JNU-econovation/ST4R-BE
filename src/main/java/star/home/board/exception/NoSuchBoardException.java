package star.home.board.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class NoSuchBoardException extends ClientException implements BoardException {
    public NoSuchBoardException() {
        super(ErrorCode.BOARD_NOT_FOUND);
    }
}
