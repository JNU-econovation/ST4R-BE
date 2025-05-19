package star.team.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record EncryptedPassword(
        @Column(name = "encrypted_password", nullable = true)
        String value
) {

    private static final Integer PASSWORD_MIN_LENGTH = 4;
    private static final Integer PASSWORD_MAX_LENGTH = 32;

    public EncryptedPassword {
        validate(value);
    }

    private void validate(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty() || value.length() < PASSWORD_MIN_LENGTH
                || value.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "비밀번호는 최소 %d자리, 최대 %d자리만 가능합니다.".formatted(PASSWORD_MIN_LENGTH,
                            PASSWORD_MAX_LENGTH));
        }
    }
}
