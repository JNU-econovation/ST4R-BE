package star.member.exception;

import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;

public class MemberNotFoundException extends InternalServerException implements MemberException {

    private final Long memberId;

    public MemberNotFoundException(Long memberId) {
        super(ErrorCode.MEMBER_NOT_FOUND);
        this.memberId = memberId;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), memberId);
    }
}
