package star.home.category.exception;

import star.common.exception.ClientException;

public class NoSuchCategoryException extends ClientException {

    public NoSuchCategoryException(String name) {
        super(name + "이라는 카테코리는 존재하지 않습니다.");
    }
}
