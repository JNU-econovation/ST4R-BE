package star.member.model.vo;

import static star.constants.MemberConstants.NICKNAME_MAX_LENGTH;
import static star.constants.MemberConstants.NICKNAME_MIN_LENGTH;

import jakarta.persistence.Embeddable;
import star.common.exception.client.BadDataLengthException;

@Embeddable
public record Nickname(String value) {

    public Nickname {
        validate(value);
    }

    public static Nickname deleted() {
        return new Nickname("탈퇴한 회원");
    }

    private void validate(String value) {
        if (value == null || value.length() < NICKNAME_MIN_LENGTH) {
            throw new BadDataLengthException("닉네임", NICKNAME_MIN_LENGTH, NICKNAME_MAX_LENGTH);
        }

        if (value.length() > NICKNAME_MAX_LENGTH) {
            throw new BadDataLengthException("닉네임", NICKNAME_MIN_LENGTH, NICKNAME_MAX_LENGTH);
        }
    }
}