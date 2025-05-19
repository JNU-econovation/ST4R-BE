package star.team.model.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record EncryptedPassword(String value) { }
