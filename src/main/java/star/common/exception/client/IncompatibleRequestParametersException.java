package star.common.exception.client;

import lombok.Getter;
import star.common.exception.ErrorCode;

@Getter
public class IncompatibleRequestParametersException extends ClientException {

    private final String option1;
    private final String option2;

    public IncompatibleRequestParametersException(String option1, String option2) {
        super(ErrorCode.INCOMPATIBLE_REQUEST_PARAMETERS);
        this.option1 = option1;
        this.option2 = option2;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), option1, option2);
    }
}
