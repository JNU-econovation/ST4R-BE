package star.team.exception;

import star.common.exception.client.Client409Exception;

public class YouAlreadyJoinedTeamException extends Client409Exception {

    private static final String ERROR_MESSAGE = "이미 모임에 참여하였습니다.";

    public YouAlreadyJoinedTeamException() {
        super(ERROR_MESSAGE);
    }
}
