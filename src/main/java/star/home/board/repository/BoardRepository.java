package star.home.board.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import star.home.board.model.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> getBoardById(Long id);

    Page<Board> getBoardsByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}