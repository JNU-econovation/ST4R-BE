package star.home.board.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.home.board.model.entity.BoardImage;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    List<BoardImage> getBoardImageByBoardIdOrderBySortOrderAsc(Long boardId);

    void deleteBoardImagesByBoardId(Long boardId);
}
