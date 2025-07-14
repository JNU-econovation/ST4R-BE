package star.team.model.entity;

import com.querydsl.core.annotations.QueryInit;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.BaseEntity;
import star.common.model.vo.Jido;
import star.member.model.entity.Member;
import star.team.model.vo.Description;
import star.team.model.vo.EncryptedPassword;
import star.team.model.vo.Name;
import star.team.model.vo.Participant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //For optimistic lock
    @Version
    private Long version;

    @Embedded
    private Name name;

    @Embedded
    private Description description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    @QueryInit({"email.value", "nickname.value"})
    private Member leader;

    @Embedded
    private Participant participant;

    @Embedded
    private EncryptedPassword encryptedPassword;

    @Column(nullable = false)
    private LocalDateTime whenToMeet;

    @Column(nullable = false)
    @QueryInit("marker")
    private Jido location;

    @Column(nullable = false)
    private Integer heartCount;

    @Builder
    public Team(Name name, Description description, Member leader, Participant participant,
            EncryptedPassword encryptedPassword, LocalDateTime whenToMeet, Jido location) {
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.participant = participant;
        this.encryptedPassword = encryptedPassword;
        this.whenToMeet = whenToMeet;
        this.location = location;
        this.heartCount = 0;
    }

    public void update(Name name, Description description, EncryptedPassword encryptedPassword,
            LocalDateTime whenToMeet, Integer maxParticipant, Jido location) {
        this.name = name;
        this.description = description;
        this.encryptedPassword = encryptedPassword;
        this.whenToMeet = whenToMeet;
        this.participant.setCapacity(maxParticipant);
        this.location = location;
    }

    public void delegateLeader(Member newLeader) {
        this.leader = newLeader;
    }

    public void increaseHeartCount() {
        this.heartCount++;
    }

    public void decreaseHeartCount() {
        this.heartCount--;
    }
}
