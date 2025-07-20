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
import star.member.constants.Constellation;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;
import star.myPage.dto.request.UpdateProfileRequest;
import star.myPage.dto.response.MyBoardResponse;
import star.myPage.dto.response.MyPageResponse;
import star.team.dto.response.GetTeamsResponse;
import star.team.service.TeamCoordinateService;
import star.team.service.internal.TeamHeartDataService;
import star.team.service.internal.TeamImageDataService;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final BoardService boardService;
    private final BoardHeartService boardHeartService;
    private final BoardImageService boardImageService;
    private final TeamHeartDataService teamHeartDataService;
    private final TeamImageDataService teamImageDataService;
    private final TeamCoordinateService teamCoordinateService;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(MemberInfoDTO memberInfoDTO) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());

        return MyPageResponse.builder()
                .birthDate(member.getBirthDate().value())
                .profileImageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname().value())
                .email(member.getEmail().getValue())
                .gender(member.getGender())
                .constellation(Constellation.fromDate(member.getBirthDate().value()))
                .build();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Slice<GetTeamsResponse> getLikedTeams(Long memberId, Pageable pageable) {
        List<GetTeamsResponse> likedTeams = teamHeartDataService
                .getForeignEntitiesOfTargetByMemberId(memberId, pageable)
                .map(team ->
                        GetTeamsResponse.from(
                                team,
                                teamImageDataService.getImageUrls(team.getId()),
                                true,
                                teamCoordinateService.isPublic(team),
                                teamCoordinateService.existsRealTeamMember(team.getId(), memberId),
                                teamCoordinateService.existsBannedTeamMember(team.getId(), memberId),
                                teamCoordinateService.isFull(team),
                                teamCoordinateService.isJoinable(team, memberId)
                        )

                )
                .toList();

        return new SliceImpl<>(likedTeams, pageable, !likedTeams.isEmpty());
    }

    @Transactional
    public void updateProfile(MemberInfoDTO memberInfoDTO, UpdateProfileRequest request) {
        memberService.updateProfile(memberInfoDTO.id(), request);
    }
}
