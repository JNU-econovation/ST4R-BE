package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class TeamMemberNotFoundException extends ClientException implements TeamException {

    public TeamMemberNotFoundException() {
        super(ErrorCode.TEAM_MEMBER_NOT_FOUND);
    }
}
