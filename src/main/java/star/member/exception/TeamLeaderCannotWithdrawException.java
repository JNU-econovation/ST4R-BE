package star.member.exception;

import java.util.List;
import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class TeamLeaderCannotWithdrawException extends ClientException implements MemberException {

    private final List<String> leaderTeamNames;


    public TeamLeaderCannotWithdrawException(List<String> leaderTeamNames) {
        super(ErrorCode.TEAM_LEADER_CANNOT_WITHDRAW_MEMBER);
        this.leaderTeamNames = leaderTeamNames;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), String.join(", ", leaderTeamNames));
    }
}
