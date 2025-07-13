package star.member.model.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.SoftDeletableEntity;
import star.member.model.vo.Email;
import star.member.model.vo.Nickname;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private Email email;

    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = false))
    private Nickname nickname;

    @Column(nullable = true)
    private String encryptedKakaoAccessToken;

    @Column(nullable = true)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(Email email, Nickname nickname, String encryptedKakaoAccessToken, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.encryptedKakaoAccessToken = encryptedKakaoAccessToken;
        this.profileImageUrl = profileImageUrl;
        this.role = Role.USER;
    }

    public void updateEncryptedKakaoAccessToken(String encryptedKakaoAccessToken) {
        this.encryptedKakaoAccessToken = encryptedKakaoAccessToken;
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }
    public void invalidateKakaoAccessToken() {
        this.encryptedKakaoAccessToken = null;
    }
}
