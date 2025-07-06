package star.team.exception;

import star.common.exception.client.ClientException;

public class CanNotBanSelfException extends ClientException {
    private static final String ERROR_MESSAGE = "자기 자신을 강퇴할 수 없습니다.";

    public CanNotBanSelfException() {
        super(ERROR_MESSAGE);
    }
}
