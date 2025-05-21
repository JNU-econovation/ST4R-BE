package star.common.exception.client;

public class InvalidPageableFieldException extends ClientException {

    private static final String ERROR_MESSAGE_PREFIX = """
            올바르지 않은 필드 입니다.
            입력한 필드 : 
            """;

    public InvalidPageableFieldException(String clientParam) {
        super(ERROR_MESSAGE_PREFIX + clientParam);
    }
}
