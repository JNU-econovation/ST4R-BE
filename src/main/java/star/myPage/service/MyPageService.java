package star.myPage.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.dto.LocalDateTimesDTO;
import star.home.board.dto.BoardImageDTO;
import star.home.board.dto.BoardSearchDTO;
import star.home.board.service.BoardHeartService;
import star.home.board.service.BoardImageService;
import star.home.board.service.BoardService;
import star.member.dto.MemberInfoDTO;
import star.myPage.dto.response.MyBoardResponse;
import star.team.dto.response.GetMyTeamsResponse;
import star.team.service.internal.TeamHeartDataService;
import star.team.service.internal.TeamImageDataService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPageService {

    private final BoardService boardService;
    private final BoardHeartService boardHeartService;
    private final BoardImageService boardImageService;
    private final TeamHeartDataService teamHeartDataService;
    private final TeamImageDataService teamImageDataService;

    public Slice<MyBoardResponse> getMyBoards(MemberInfoDTO memberInfoDTO, Pageable pageable) {

        BoardSearchDTO searchDTO = BoardSearchDTO.builder()
                .authorName(memberInfoDTO.email().getValue())
                .localDateTimesForSearch(
                        LocalDateTimesDTO.builder()
                                .start(LocalDateTime.of(1970, 1, 1, 0, 0))
                                .end(LocalDateTime.now())
                                .build()
                )
                .build();

        List<MyBoardResponse> myBoard = boardService
                .getBoardPeeks(memberInfoDTO, searchDTO, pageable)
                .stream()
                .filter(boardPeek -> boardPeek.authorId().equals(memberInfoDTO.id()))
                .map(MyBoardResponse::from)
                .toList();

        return new SliceImpl<>(myBoard, pageable, !myBoard.isEmpty());
    }

    public Slice<MyBoardResponse> getLikedBoards(Long memberId, Pageable pageable) {
        List<MyBoardResponse> likedBoards = boardHeartService
                .getForeignEntitiesOfTargetByMemberId(memberId, pageable)
                .map(board -> {
                            List<BoardImageDTO> images = boardImageService.getImageUrls(board.getId());
                            String thumbnailUrl = images.isEmpty() ? null : images.getFirst().imageUrl();
                            return MyBoardResponse.fromForLikedBoard(board, thumbnailUrl);
                        }
                )
                .toList();

        return new SliceImpl<>(likedBoards, pageable, !likedBoards.isEmpty());
    }

    public Slice<GetMyTeamsResponse> getLikedTeams(Long memberId, Pageable pageable) {
        List<GetMyTeamsResponse> likedTeams = teamHeartDataService
                .getForeignEntitiesOfTargetByMemberId(memberId, pageable)
                .map(team -> {
                            List<String> images = teamImageDataService.getImageUrls(team.getId());
                            String thumbnailUrl = images.isEmpty() ? null : images.getFirst();
                            return GetMyTeamsResponse.from(team, thumbnailUrl);
                        }
                )
                .toList();

        return new SliceImpl<>(likedTeams, pageable, !likedTeams.isEmpty());
    }
}
