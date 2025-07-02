package star.home.service;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import star.common.dto.LocalDateTimesDTO;
import star.common.util.CommonTimeUtils;
import star.home.board.service.BoardService;
import star.home.category.model.vo.CategoryName;
import star.home.constants.Period;
import star.home.dto.request.LobbyRequest;
import star.home.dto.response.LobbyBoardResponse;
import star.member.dto.MemberInfoDTO;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final BoardService boardService;

    public LobbyBoardResponse getLobbyBoards(
            @Nullable MemberInfoDTO memberInfoDTO,
            LobbyRequest request,
            Pageable pageable
    ) {

        Period period = setPeriodIfNull(request.period());
        List<CategoryName> categories = setCategoriesIfNullOrEmpty(request.categories());

        LocalDateTimesDTO localDateTimesDTO = CommonTimeUtils.getLocalDateTimesByPeriod(period);

        return LobbyBoardResponse.builder()
                .boardPeeks(
                        boardService.getBoardPeeks(
                                memberInfoDTO,
                                request,
                                localDateTimesDTO.start(), localDateTimesDTO.end(),
                                pageable
                        )
                )
                .build();
    }

    private Period setPeriodIfNull(Period period) {
        return (period == null) ? Period.WEEKLY : period;
    }

    private List<CategoryName> setCategoriesIfNullOrEmpty(List<CategoryName> categories) {
        return (categories == null || categories.isEmpty())
                ? List.of(CategoryName.values()) : categories;
    }
}
