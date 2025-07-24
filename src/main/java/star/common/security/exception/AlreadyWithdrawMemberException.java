package star.common.security.exception;


import star.common.exception.ErrorCode;

public class AlreadyWithdrawMemberException extends CustomAccessDeniedException{

    public AlreadyWithdrawMemberException() {
        super(ErrorCode.ALREADY_WITHDRAW_ERROR);
    }

}
