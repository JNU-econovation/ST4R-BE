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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.SoftDeletableEntity;
import star.member.model.vo.BirthDate;
import star.member.model.vo.Email;
import star.member.model.vo.Gender;
import star.member.model.vo.MemberStatus;
import star.member.model.vo.Nickname;
import star.member.model.vo.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private Email email;

    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = true, unique = true))
    private Nickname nickname;

    @AttributeOverride(name = "value", column = @Column(name = "birthDate", nullable = true))
    private BirthDate birthDate;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = true)
    private String encryptedKakaoAccessToken;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(Email email) {
        this.email = email;
        this.role = Role.USER;
        this.status = MemberStatus.REGISTERING;
    }

    public void completeRegistration(
            LocalDate birthDate, Gender gender, String nickname, String profileImageUrl
    ) {
        this.birthDate = new BirthDate(birthDate);
        this.gender = gender;
        this.nickname = new Nickname(nickname);
        this.profileImageUrl = profileImageUrl;
        this.status = MemberStatus.REGISTER_COMPLETED;
    }

    public void updateEncryptedKakaoAccessToken(String encryptedKakaoAccessToken) {
        this.encryptedKakaoAccessToken = encryptedKakaoAccessToken;
    }

    public void updateProfile(Nickname nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void invalidateKakaoAccessToken() {
        this.encryptedKakaoAccessToken = null;
    }


    public void inactivate() {
        this.status = MemberStatus.INACTIVATED;
        this.birthDate = BirthDate.deleted();
        this.email = Email.deleted();
        this.gender = Gender.UNKNOWN;
        this.profileImageUrl = null;

        invalidateKakaoAccessToken();
        demoteToUser();
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    public void demoteToUser() {
        this.role = Role.USER;
    }
}
