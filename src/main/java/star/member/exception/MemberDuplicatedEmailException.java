package star.member.exception;

import star.common.exception.ClientException;

public class MemberDuplicatedEmailException extends ClientException {
    private static final String ERROR_MESSAGE = "중복된 이메일입니다.";
    public MemberDuplicatedEmailException() {
        super(ERROR_MESSAGE);
    }
}
