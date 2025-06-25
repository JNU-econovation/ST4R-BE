package star.team.chat.model.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.SoftDeletableEntity;
import star.team.chat.model.vo.Message;
import star.team.model.entity.TeamMember;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TeamMember teamMember;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "message", nullable = false, length = 10240))
    private Message message;

    @Builder
    public Chat(TeamMember teamMember, String message) {
        this.teamMember = teamMember;
        this.message = new Message(message);
    }
}
