package star.home.category.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class NoSuchCategoryException extends ClientException implements CategoryException {
    private final String requestCategory;

    public NoSuchCategoryException(String requestCategory) {
        super(ErrorCode.CATEGORY_NOT_FOUND);
        this.requestCategory = requestCategory;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), requestCategory);
    }
}
