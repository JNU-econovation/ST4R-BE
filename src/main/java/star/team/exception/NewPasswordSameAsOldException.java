package star.team.exception;

import star.common.exception.client.Client409Exception;

public class NewPasswordSameAsOldException extends Client409Exception {

    private static final String ERROR_MESSAGE = "새 비밀번호가 이전 비밀번호와 같습니다.";

    public NewPasswordSameAsOldException() {
        super(ERROR_MESSAGE);
    }
}