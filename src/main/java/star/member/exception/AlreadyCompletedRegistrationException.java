package star.member.exception;

import star.common.exception.client.Client409Exception;

public class AlreadyCompletedRegistrationException extends Client409Exception {

    private static final String ERROR_MESSAGE = "이미 가입을 완료했습니다.";

    public AlreadyCompletedRegistrationException() {
        super(ERROR_MESSAGE);
    }
}