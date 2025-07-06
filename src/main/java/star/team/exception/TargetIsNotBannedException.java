package star.team.exception;

import star.common.exception.client.ClientException;

public class TargetIsNotBannedException extends ClientException {
    private static final String ERROR_MESSAGE = "강퇴되지 않은 회원입니다.";

    public TargetIsNotBannedException() {
        super(ERROR_MESSAGE);
    }
}
