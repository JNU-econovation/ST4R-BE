package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class TeamLeaderSelfDelegatingException extends ClientException implements TeamException {

    public TeamLeaderSelfDelegatingException() {
        super(ErrorCode.TEAM_LEADER_SELF_DELEGATING);
    }
}
