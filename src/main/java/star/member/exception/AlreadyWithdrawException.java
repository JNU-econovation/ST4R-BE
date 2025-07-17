package star.member.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class AlreadyWithdrawException extends ClientException implements MemberException {

    public AlreadyWithdrawException() {
        super(ErrorCode.ALREADY_WITHDRAW_REGISTRATION);
    }
}