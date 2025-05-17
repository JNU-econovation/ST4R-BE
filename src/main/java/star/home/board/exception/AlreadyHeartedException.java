package star.home.board.exception;

import star.common.exception.client.Client409Exception;

public class AlreadyHeartedException extends Client409Exception {
    private static final String ERROR_MESSAGE = "이미 좋아요를 하였습니다.";

    public AlreadyHeartedException() {
        super(ERROR_MESSAGE);
    }
}
