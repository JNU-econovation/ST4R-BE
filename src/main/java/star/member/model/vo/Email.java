package star.member.model.vo;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.exception.client.BadDataSyntaxException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {
    private static final String DELETED_EMAIL = "deleted@deleted.com";

    private String value;

    public Email(String value) {
        validateEmail(value);
        this.value = value;
    }

    public static Email deleted() {
        return new Email(DELETED_EMAIL);
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    

    private void validateEmail(String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new BadDataSyntaxException("올바른 이메일 형식이 아닙니다.");
        }
    }
}
