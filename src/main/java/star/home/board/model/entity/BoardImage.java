package star.home.board.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import star.common.entity.SoftDeletableEntity;

@Entity
@RequiredArgsConstructor
@Getter
public class BoardImage extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String imageUrl;

    private Integer sortOrder;

    @Builder
    public BoardImage(Board board, String imageUrl, Integer sortOrder) {
        this.board = board;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}
