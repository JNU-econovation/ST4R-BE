package star.home.dto.response;

import java.util.List;
import star.home.dto.BoardPeekDTO;

public record LobbyResponse(
        List<BoardPeekDTO> boardPeeks
) {

}
