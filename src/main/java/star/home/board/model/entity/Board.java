package star.home.board.model.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.BaseEntity;
import star.home.board.model.vo.Content;
import star.home.board.model.vo.Title;
import star.home.category.model.entity.Category;
import star.member.model.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //For optimistic lock
    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false))
    private Title title;

    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer viewCount;

    @Column(nullable = false)
    private Integer heartCount;

    @Column(nullable = false)
    private Integer commentCount;

    @Builder
    public Board(Member member, String title, Content content, Category category) {
        this.member = member;
        this.title = new Title(title);
        this.content = content;
        this.category = category;
        this.viewCount = 0;
        this.heartCount = 0;
        this.commentCount = 0;
    }

    public void update(String title, Content content, Category category) {
        this.title = new Title(title);
        this.content = content;
        this.category = category;
    }


    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void increaseHeartCount() {
        this.heartCount++;
    }

    public void decreaseHeartCount() {
        this.heartCount--;
    }
}
