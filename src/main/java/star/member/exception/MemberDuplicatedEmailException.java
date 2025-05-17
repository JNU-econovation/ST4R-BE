package star.member.exception;

import star.common.exception.client.Client409Exception;

public class MemberDuplicatedEmailException extends Client409Exception {
    private static final String ERROR_MESSAGE = "중복된 이메일입니다.";
    public MemberDuplicatedEmailException() {
        super(ERROR_MESSAGE);
    }
}
