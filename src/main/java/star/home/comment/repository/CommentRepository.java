package star.home.comment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import star.home.comment.model.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> getCommentByIdAndBoardId(Long id, Long boardId);
    List<Comment> getCommentsByBoardIdAndDepthOrderByCreatedAtAsc(Long boardId, Integer depth);

    @Query("select max(c.depth) from Comment c where c.board.id = :boardId")
    Integer findTopDepthByBoardId(@Param("boardId") Long boardId);

    void deleteCommentsByBoardId(Long boardId);
}
