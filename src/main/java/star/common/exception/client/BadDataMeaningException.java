package star.common.exception.client;

import lombok.Getter;
import star.common.exception.ErrorCode;

@Getter
public class BadDataMeaningException extends ClientException {

    private final String message;

    public BadDataMeaningException(String message) {
        super(ErrorCode.BAD_DATA_MEANING);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), message);
    }
}
