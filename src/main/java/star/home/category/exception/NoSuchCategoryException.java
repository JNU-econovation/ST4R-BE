package star.home.category.exception;

import star.common.exception.client.ClientException;

public class NoSuchCategoryException extends ClientException {
    private static final String ERROR_POSTFIX_MESSAGE = " 이라는 카테고리는 존재하지 않습니다.";
    public NoSuchCategoryException(String name) {
        super(name + ERROR_POSTFIX_MESSAGE);
    }
}
