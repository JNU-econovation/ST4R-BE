package star.common.exception.client;

import lombok.Getter;

@Getter
public class BadDataLengthException extends IllegalArgumentException {

    private static final String ERROR_MESSAGE = "%s의 길이는 %d자 부터 %d자 까지 가능합니다.";
    private final Integer minLength;
    private final Integer maxLength;
    private final String fieldName;

    public BadDataLengthException(String fieldName, Integer minLength, Integer maxLength) {
        super(ERROR_MESSAGE.formatted(fieldName, minLength, maxLength));
        this.fieldName = fieldName;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }
}
