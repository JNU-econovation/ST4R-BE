package star.home.comment.model.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import lombok.Setter;
import star.common.entity.SoftDeletableEntity;
import star.home.board.model.entity.Board;
import star.home.comment.model.vo.Content;
import star.member.model.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_comment_id", nullable = false)
    private Comment rootComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parrent_comment_id", nullable = true)
    private Comment parentComment;

    private Integer depth;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "content", nullable = false))
    private Content content;

    @Builder
    public Comment(Member author, Board board, Comment parentComment, Integer depth,
            String content) {
        this.author = author;
        this.board = board;
        this.parentComment = parentComment;
        this.depth = depth;
        this.content = new Content(content);
    }

    public void updateComment(String content) {
        this.content = new Content(content);
    }

}
