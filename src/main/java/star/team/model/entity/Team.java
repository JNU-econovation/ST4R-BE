package star.team.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.team.model.vo.Description;
import star.team.model.vo.Name;
import star.team.model.vo.Participant;
import star.team.model.vo.EncryptedPassword;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Description description;

    @Column(nullable = false)
    private Long leaderId;

    @Embedded
    private Participant participant;

    @Embedded
    private EncryptedPassword encryptedPassword;

    @Column(nullable = false)
    private LocalDateTime whenToMeet;

    @Builder
    public Team(Name name, Description description, Long leaderId, Participant participant,
            EncryptedPassword encryptedPassword, LocalDateTime whenToMeet) {
        this.name = name;
        this.description = description;
        this.leaderId = leaderId;
        this.participant = participant;
        this.encryptedPassword = encryptedPassword;
        this.whenToMeet = whenToMeet;
    }
}
