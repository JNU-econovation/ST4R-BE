package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class TeamNotFoundException extends ClientException implements TeamException {
    public TeamNotFoundException() {
        super(ErrorCode.TEAM_NOT_FOUND);
    }
}
