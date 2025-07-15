package star.member.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;

public class MemberDuplicatedFieldException extends ClientException implements MemberException {
    private final String duplicatedField;

    public MemberDuplicatedFieldException(String duplicatedField) {
        super(ErrorCode.MEMBER_DUPLICATED_FIELD);
        this.duplicatedField = duplicatedField;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), duplicatedField);
    }
}
