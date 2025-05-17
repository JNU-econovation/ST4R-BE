package star.home.board.exception;

import star.common.exception.client.ClientException;

public class NoSuchBoardException extends ClientException {
    public NoSuchBoardException() {
        super("유효하지 않은 게시글 입니다.");
    }
}
