package star.member.exception;

import star.common.exception.InternalServerException;

public class MemberNotFoundException extends InternalServerException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
