package star.team.exception;

import star.common.exception.client.Client409Exception;

public class TeamLeaderSelfDelegatingException extends Client409Exception {
    private static final String ERROR_MESSAGE = "자기 자신에게 모임장을 위임할 수 없습니다.";


    public TeamLeaderSelfDelegatingException() {
        super(ERROR_MESSAGE);
    }
}
