package star.home.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;
import star.home.dto.BoardPeekDTO;

@Builder
public record LobbyBoardResponse(
        Page<BoardPeekDTO> boardPeeks
) {

}
