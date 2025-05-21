package star.home.comment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.home.comment.model.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> getCommentByIdAndBoardId(Long id, Long boardId);

    void deleteCommentsByBoardId(Long boardId);

    Boolean existsByBoardId(Long boardId);

    List<Comment> getBoardsByBoardIdAndDepth(Long boardId, Integer rootCommentDepth,
            Pageable pageable);

    List<Comment> getCommentsByBoardIdAndRootCommentIdInAndDepthNot(Long boardId,
            List<Long> rootCommentIds, Integer rootCommentDepth);
}
