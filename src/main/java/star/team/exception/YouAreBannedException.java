package star.team.exception;

import star.common.exception.client.Client403Exception;

public class YouAreBannedException extends Client403Exception {
    private static final String ERROR_MESSAGE = "강퇴된 방에는 다시 참여할 수 없습니다.";

    public YouAreBannedException() {
        super(ERROR_MESSAGE);
    }
}
