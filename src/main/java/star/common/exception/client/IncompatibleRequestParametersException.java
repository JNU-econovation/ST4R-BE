package star.common.exception.client;

public class IncompatibleRequestParametersException extends Client409Exception {

    private static final String ERROR_MESSAGE = "%s 와(과) %s 옵션은 동시에 사용할 수 없습니다.";

    public IncompatibleRequestParametersException(String option1, String option2) {
        super(ERROR_MESSAGE.formatted(option1, option2));
    }
}
