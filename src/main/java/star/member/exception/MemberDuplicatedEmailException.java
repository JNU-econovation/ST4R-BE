package star.member.exception;

public class MemberDuplicatedEmailException extends MemberException {
    private static final String ERROR_MESSAGE = "중복된 이메일입니다.";
    public MemberDuplicatedEmailException() {
        super(ERROR_MESSAGE);
    }
}
