package star.home.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import star.home.board.dto.BoardSearchDTO;
import star.home.board.model.entity.Board;

public interface BoardRepositoryCustom {

    Page<Board> searchBoards(BoardSearchDTO searchDTO, Pageable pageable);

}
