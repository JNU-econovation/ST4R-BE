package star.home.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.BoardHeart;

@Repository
public interface BoardHeartRepository extends JpaRepository<BoardHeart, Long> {
    void deleteHeartsByBoardId(Long boardId);

    boolean existsByMemberIdAndBoardId(Long memberId, Long boardId);

    void deleteHeartByMemberIdAndBoardId(Long memberId, Long boardId);

    @Query("SELECT bh.board FROM BoardHeart bh WHERE bh.member.id = :memberId")
    Page<Board> findBoardsByMemberId(Long memberId, Pageable pageable);
}
