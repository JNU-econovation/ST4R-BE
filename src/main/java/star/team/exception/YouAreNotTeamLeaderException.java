package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class YouAreNotTeamLeaderException extends ClientException implements TeamException {

    public YouAreNotTeamLeaderException() {
        super(ErrorCode.YOU_ARE_NOT_TEAM_LEADER);
    }
}
