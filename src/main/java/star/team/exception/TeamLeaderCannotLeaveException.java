package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class TeamLeaderCannotLeaveException extends ClientException implements TeamException {

    public TeamLeaderCannotLeaveException() {
        super(ErrorCode.TEAM_LEADER_CANNOT_LEAVE);
    }
}
