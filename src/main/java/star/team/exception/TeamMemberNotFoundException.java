package star.team.exception;

import star.common.exception.client.ClientException;

public class TeamMemberNotFoundException extends ClientException {
    
    private static final String ERROR_MESSAGE = "해당 회원이 모임에 속해있지 않습니다.";

    public TeamMemberNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
