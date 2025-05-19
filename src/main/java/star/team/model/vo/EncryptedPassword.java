package star.team.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record EncryptedPassword(
        @Column(name = "encrypted_password", nullable = true)
        String value
) { }
