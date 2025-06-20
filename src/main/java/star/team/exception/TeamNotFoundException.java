package star.team.exception;

import star.common.exception.client.ClientException;

public class TeamNotFoundException extends ClientException {
    private static final String ERROR_MESSAGE = "올바르지 않은 모임 아이디 입니다.";

    public TeamNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
