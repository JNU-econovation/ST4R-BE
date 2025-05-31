package star.home.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import star.common.dto.LocalDateTimesDTO;
import star.common.util.CommonTimeUtils;
import star.home.board.service.BoardService;
import star.home.constants.Period;
import star.home.dto.response.LobbyBoardResponse;
import star.member.dto.MemberInfoDTO;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final BoardService boardService;

    public LobbyBoardResponse getLobbyBoards(@Nullable MemberInfoDTO memberInfoDTO, Period period,
            Pageable pageable) {

        LocalDateTimesDTO localDateTimesDTO = CommonTimeUtils.getLocalDateTimesByPeriod(period);

        return LobbyBoardResponse.builder()
                .boardPeeks(boardService.getBoardPeeks(memberInfoDTO, localDateTimesDTO.start(),
                        localDateTimesDTO.end(), pageable))
                .build();
    }
}
