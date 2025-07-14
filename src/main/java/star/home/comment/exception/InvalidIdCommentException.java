package star.home.comment.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class InvalidIdCommentException extends ClientException implements CommentException {
    public InvalidIdCommentException() {
        super(ErrorCode.INVALID_ID_COMMENT);
    }
}
