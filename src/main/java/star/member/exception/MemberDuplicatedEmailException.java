package star.member.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class MemberDuplicatedEmailException extends ClientException implements MemberException {
    public MemberDuplicatedEmailException() {
        super(ErrorCode.MEMBER_DUPLICATED_EMAIL);
    }
}
