package star.team.exception;

import star.common.exception.client.Client409Exception;

public class TeamLeaderCannotLeaveException extends Client409Exception {

    private static final String ERROR_MESSAGE = "모임장은 모임을 탈퇴할 수 없습니다. 모임장 권한을 다른 사람에게 위임하거나, 팀 자체를 삭제해주세요.";

    public TeamLeaderCannotLeaveException() {
        super(ERROR_MESSAGE);
    }
}
