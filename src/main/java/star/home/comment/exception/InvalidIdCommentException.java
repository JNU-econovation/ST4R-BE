package star.home.comment.exception;

import star.common.exception.client.ClientException;

public class InvalidIdCommentException extends ClientException {
    private static final String ERROR_MESSAGE = "게시글 또는 부모 댓글 아이디가 유효하지 않습니다.";

    public InvalidIdCommentException() {
        super(ERROR_MESSAGE);
    }
}
