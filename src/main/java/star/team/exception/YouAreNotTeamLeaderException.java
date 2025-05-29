package star.team.exception;

import star.common.exception.client.Client403Exception;

public class YouAreNotTeamLeaderException extends Client403Exception {

    private static final String ERROR_MESSAGE = "모임장만 모임을 수정 및 삭제할 수 있습니다.";


    public YouAreNotTeamLeaderException() {
        super(ERROR_MESSAGE);
    }
}
