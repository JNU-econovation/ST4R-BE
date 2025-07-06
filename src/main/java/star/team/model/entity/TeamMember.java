package star.team.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.SoftDeletableEntity;
import star.member.model.entity.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeamMember extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @Column(nullable = false)
    private Boolean isBanned;

    @Builder
    public TeamMember(Team team, Member member) {
        this.team = team;
        this.member = member;
        this.isBanned = false;
    }

    public void ban() {
        this.isBanned = true;
        markAsDeprecated();
    }

    public void unban() {
        this.isBanned = false;
    }
}
