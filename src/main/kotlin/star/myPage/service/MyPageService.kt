package star.myPage.service

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import star.common.dto.LocalDateTimesDTO
import star.home.board.dto.BoardSearchDTO
import star.home.board.service.BoardHeartService
import star.home.board.service.BoardImageService
import star.home.board.service.BoardService
import star.member.constants.Constellation
import star.member.dto.MemberInfoDTO
import star.member.service.MemberService
import star.myPage.dto.request.UpdateProfileRequest
import star.myPage.dto.response.MyBoardResponse
import star.myPage.dto.response.MyPageResponse
import star.team.dto.response.GetTeamsResponse
import star.team.service.TeamCoordinateService
import star.team.service.internal.TeamHeartDataService
import star.team.service.internal.TeamImageDataService
import java.time.LocalDateTime

@Service
class MyPageService(
    private val boardService: BoardService,
    private val boardHeartService: BoardHeartService,
    private val boardImageService: BoardImageService,
    private val teamHeartDataService: TeamHeartDataService,
    private val teamImageDataService: TeamImageDataService,
    private val teamCoordinateService: TeamCoordinateService,
    private val memberService: MemberService
) {
    @Transactional(readOnly = true)
    fun getMyPage(memberInfoDTO: MemberInfoDTO): MyPageResponse {
        val member = memberService.getMemberEntityById(memberInfoDTO.id)

        return MyPageResponse(
            birthDate = member.getBirthDate().value(),
            profileImageUrl = member.getProfileImageUrl(),
            nickname = member.getNickname().value(),
            email = member.getEmail().getValue(),
            gender = member.gender,
            constellation = Constellation.fromDate(member.birthDate.value)
        )

    }

    @Transactional(readOnly = true)
    fun getMyBoards(memberInfoDTO: MemberInfoDTO, pageable: Pageable): Slice<MyBoardResponse> {
        val searchDTO: BoardSearchDTO = BoardSearchDTO.builder()
            .authorName(memberInfoDTO.email.value)
            .localDateTimesForSearch(
                LocalDateTimesDTO.builder()
                    .start(LocalDateTime.of(1970, 1, 1, 0, 0))
                    .end(LocalDateTime.now())
                    .build()
            )
            .build()

        val myBoard = boardService.getBoardPeeks(memberInfoDTO, searchDTO, pageable)
            .asSequence()
            .filter { boardPeek -> boardPeek.authorId == (memberInfoDTO.id()) }
            .map(MyBoardResponse::from)
            .toList()

        return SliceImpl(myBoard, pageable, myBoard.isNotEmpty())
    }

    @Transactional(readOnly = true)
    fun getLikedBoards(memberId: Long, pageable: Pageable): Slice<MyBoardResponse> {
        val likedBoards = boardHeartService
            .getForeignEntitiesOfTargetByMemberId(memberId, pageable)
            .map { board ->
                val thumbnailUrl = boardImageService.getImageUrls(board.id)
                    ?.firstOrNull()
                    ?.imageUrl()
                MyBoardResponse.fromForLikedBoard(board, thumbnailUrl)
            }.toList()

        return SliceImpl(likedBoards, pageable, likedBoards.isNotEmpty())
    }

    @Transactional(readOnly = true)
    fun getLikedTeams(memberId: Long, pageable: Pageable): Slice<GetTeamsResponse> {
        val likedTeams = teamHeartDataService
            .getForeignEntitiesOfTargetByMemberId(memberId, pageable)
            .map { team ->
                GetTeamsResponse.from(
                    team,
                    teamImageDataService.getImageUrls(team.id),
                    true,
                    teamCoordinateService.isPublic(team),
                    teamCoordinateService.existsRealTeamMember(team.id, memberId),
                    teamCoordinateService.existsBannedTeamMember(team.id, memberId),
                    teamCoordinateService.isFull(team),
                    teamCoordinateService.isJoinable(team, memberId)
                )
            }
            .toList()

        return SliceImpl(likedTeams, pageable, likedTeams.isNotEmpty())
    }

    @Transactional
    fun updateProfile(memberInfoDTO: MemberInfoDTO, request: UpdateProfileRequest) =
        memberService.updateProfile(memberInfoDTO.id(), request)
}
