package star.team.chat.model.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private Long redisId;

    @ManyToOne
    private TeamMember teamMember;

    @Column(nullable = false, updatable = false)
    private LocalDateTime chattedAt;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "message", nullable = false, length = 10240))
    private Message message;

    @Builder
    public Chat(TeamMember teamMember, Long redisId, LocalDateTime chattedAt, String message) {
        this.teamMember = teamMember;
        this.redisId = redisId;
        this.chattedAt = chattedAt;
        this.message = new Message(message);
    }
}
