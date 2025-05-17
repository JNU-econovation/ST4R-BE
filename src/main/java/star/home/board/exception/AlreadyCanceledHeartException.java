package star.home.board.exception;

import star.common.exception.client.Client409Exception;

public class AlreadyCanceledHeartException extends Client409Exception {
    private static final String ERROR_MESSAGE = "이미 좋아요 취소를 하였습니다.";

    public AlreadyCanceledHeartException() {
        super(ERROR_MESSAGE);
    }
}
