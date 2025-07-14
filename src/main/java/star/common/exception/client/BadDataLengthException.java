package star.common.exception.client;

import lombok.Getter;
import star.common.exception.ErrorCode;

@Getter
public class BadDataLengthException extends ClientException {

    private final String fieldName;
    private final int minLength;
    private final int maxLength;

    public BadDataLengthException(String fieldName, int minLength, int maxLength) {
        super(ErrorCode.BAD_DATA_MEANING);
        this.fieldName = fieldName;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public String getMessage() {
        return String.format(
                getErrorCode().getMessage(),
                "%s의 길이는 %d자 부터 %d자 까지 가능합니다.".formatted(fieldName, minLength, maxLength)
        );
    }
}
