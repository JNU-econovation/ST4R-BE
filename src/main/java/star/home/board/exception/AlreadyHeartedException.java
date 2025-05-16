package star.home.board.exception;

import star.common.exception.ClientException;

public class AlreadyHeartedException extends ClientException {
    private static final String ERROR_MESSAGE = "이미 좋아요를 하였습니다.";

    public AlreadyHeartedException() {
        super(ERROR_MESSAGE);
    }
}
