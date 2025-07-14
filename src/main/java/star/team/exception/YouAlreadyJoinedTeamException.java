package star.team.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class YouAlreadyJoinedTeamException extends ClientException implements TeamException {

    public YouAlreadyJoinedTeamException() {
        super(ErrorCode.YOU_ALREADY_JOINED_TEAM);
    }
}
