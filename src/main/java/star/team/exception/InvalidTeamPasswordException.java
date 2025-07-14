package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class InvalidTeamPasswordException extends ClientException implements TeamException {
    public InvalidTeamPasswordException() {
        super(ErrorCode.INVALID_TEAM_PASSWORD);
    }
}
