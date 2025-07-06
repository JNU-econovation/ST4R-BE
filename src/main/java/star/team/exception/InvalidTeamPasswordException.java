package star.team.exception;

import star.common.exception.client.Client403Exception;

public class InvalidTeamPasswordException extends Client403Exception {
    private static final String ERROR_MESSAGE = "올바르지 않은 모임 비밀번호 입니다.";

    public InvalidTeamPasswordException() {
        super(ERROR_MESSAGE);
    }
}
