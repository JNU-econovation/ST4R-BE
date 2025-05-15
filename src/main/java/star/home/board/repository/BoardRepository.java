package star.home.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import star.home.board.model.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
