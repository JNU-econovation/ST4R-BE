package star.home.board.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import star.home.board.model.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    Optional<Board> getBoardById(Long id);

    Slice<Board> findAllByMemberId(Long memberId, Pageable pageable);
}
