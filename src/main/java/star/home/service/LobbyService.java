package star.home.service;

import jakarta.annotation.Nullable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import star.common.exception.server.InternalServerException;
import star.common.util.CommonUtils;
import star.home.board.service.BoardService;
import star.home.constants.Period;
import star.common.constants.SortField;
import star.home.dto.response.LobbyBoardResponse;
import star.member.dto.MemberInfoDTO;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private static final List<SortField> ALLOWED_SORT_FIELDS = List.of(SortField.CREATED_AT,
            SortField.HEART_COUNT, SortField.VIEW_COUNT);

    private final BoardService boardService;

    public LobbyBoardResponse getLobbyBoards(@Nullable MemberInfoDTO memberInfoDTO, Period period,
            Pageable pageable) {

        pageable = CommonUtils.convertSortForDb(ALLOWED_SORT_FIELDS, pageable);

        LocalDateTime start, end;

        switch (period) {
            case DAILY -> {
                start = LocalDate.now().atStartOfDay();
                end = start.plusDays(1);
            }

            case WEEKLY -> {
                LocalDate today = LocalDate.now();
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                start = monday.atStartOfDay();
                end = start.plusWeeks(1);
            }

            case MONTHLY -> {
                LocalDate today = LocalDate.now();
                LocalDate firstDayOfMonth = today.withDayOfMonth(1);
                start = firstDayOfMonth.atStartOfDay();
                end = start.plusMonths(1);
            }

            case YEARLY -> {
                LocalDate today = LocalDate.now();
                LocalDate firstDayOfYear = today.withDayOfYear(1);
                start = firstDayOfYear.atStartOfDay();
                end = start.plusYears(1);
            }

            default -> {
                throw new InternalServerException("period 값이 예상치 못한 값입니다 -> %s".formatted(
                        Objects.requireNonNull(period, "null").toString()));
            }
        }

        return LobbyBoardResponse.builder()
                .boardPeeks(boardService.getBoardPeeks(memberInfoDTO, start, end, pageable))
                .build();
    }
}
