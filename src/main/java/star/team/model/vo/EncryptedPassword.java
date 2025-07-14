package star.team.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;

@Slf4j
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EncryptedPassword {
    private static final Pattern BCRYPT_PATTERN = Pattern.compile(
            "^\\$2[aby]\\$\\d{2}\\$.*"
    );

    @Column(name = "encrypted_password", nullable = true)
    private String value;

    public EncryptedPassword(String value) {
        validateEncryptedPassword(value);
        this.value = value;
    }

    private void validateEncryptedPassword(String value) {
        if (value == null || !BCRYPT_PATTERN.matcher(value).matches()) {
            log.error("BCrypt로 암호화가 되지 않는 버그 발견");
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
