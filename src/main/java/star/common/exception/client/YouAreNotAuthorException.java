package star.common.exception.client;

public class YouAreNotAuthorException extends Client403Exception {
    private static final String ERROR_MESSAGE = "작성자만 삭제할 수 있습니다.";
    public YouAreNotAuthorException() {
        super(ERROR_MESSAGE);
    }
}
