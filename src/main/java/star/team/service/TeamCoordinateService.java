package star.team.service;

import static star.common.constants.CommonConstants.ANONYMOUS_MEMBER_ID;

import jakarta.annotation.Nullable;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.dto.response.internal.Author;
import star.common.util.CommonUtils;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;
import star.team.dto.TeamDTO;
import star.team.dto.request.TeamLeaderDelegateRequest;
import star.team.dto.request.TeamRequest;
import star.team.dto.response.TeamDetailsResponse;
import star.team.exception.TeamLeaderCannotLeaveException;
import star.team.exception.TeamLeaderSelfDelegatingException;
import star.team.exception.TeamMemberNotFoundException;
import star.team.exception.YouAreNotTeamLeaderException;
import star.team.model.entity.Team;
import star.team.model.vo.Description;
import star.team.model.vo.Name;
import star.team.model.vo.PlainPassword;
import star.team.service.internal.TeamDataService;
import star.team.service.internal.TeamHeartDataService;
import star.team.service.internal.TeamImageDataService;
import star.team.service.internal.TeamMemberDataService;

@Service
@RequiredArgsConstructor
public class TeamCoordinateService {

    private final MemberService memberService;
    private final TeamHeartDataService teamHeartDataService;
    private final TeamDataService teamDataService;
    private final TeamImageDataService teamImageDataService;
    private final TeamMemberDataService teamMemberDataService;

    @Transactional
    public Long createTeam(MemberInfoDTO memberInfoDTO, TeamRequest request) {
    
        //todo: 인당 만들 수 있는 & 들어갈 수 있는 팀 개수 제한시키기
        
        Long memberId = memberInfoDTO.id();
        TeamDTO teamDTO = TeamDTO.builder()
                .name(new Name(request.name()))
                .location(request.location())
                .whenToMeet(request.whenToMeet())
                .plainPassword(new PlainPassword(request.password()))
                .maxParticipantCount(request.maxParticipantCount())
                .description(new Description(request.description()))
                .build();

        Team createdTeam = teamDataService.createTeam(memberId, teamDTO);
        teamImageDataService.addImageUrls(createdTeam, request.imageUrls());
        teamMemberDataService.addTeamMember(createdTeam, memberId);

        return createdTeam.getId();
    }

    @Transactional(readOnly = true)
    public TeamDetailsResponse getTeamDetails(Long teamId, @Nullable MemberInfoDTO memberInfoDTO) {
        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;
        Team team = teamDataService.getTeamEntityById(teamId);
        MemberInfoDTO authorInfo = memberService.getMemberById(team.getLeaderId());
        Long authorId = authorInfo.id();

        return TeamDetailsResponse.builder()
                .id(teamId)
                .author(Author.builder()
                        .id(authorId)
                        .nickname(authorInfo.email().value())
                        .imageUrl(authorInfo.profileImageUrl())
                        .build())
                .isViewerAuthor(Objects.equals(authorId, viewerId))
                .imageUrls(teamImageDataService.getImageUrls(teamId))
                .name(team.getName().value())
                .description(team.getDescription().value())
                .location(team.getLocation())
                .whenToMeet(CommonUtils.convertLocalDateTimeToOffsetDateTime(team.getWhenToMeet()))
                .nowParticipants(team.getParticipant().getCurrent())
                .maxParticipants(team.getParticipant().getCapacity())
                .createdAt(CommonUtils.convertLocalDateTimeToOffsetDateTime(team.getCreatedAt()))
                .likeCount(team.getHeartCount())
                .liked(teamHeartDataService.hasHearted(viewerId, teamId))
                .isPublic(isPublic(team))
                .isJoinable(isJoinable(team))
                .build();
    }

    @Transactional
    public void updateTeam(MemberInfoDTO memberInfoDTO, Long teamId, TeamRequest request) {
        Team team = teamDataService.getTeamEntityById(teamId);

        assertTeamLeader(memberInfoDTO, team);

        TeamDTO teamDTO = TeamDTO.builder()
                .name(new Name(request.name()))
                .location(request.location())
                .whenToMeet(request.whenToMeet())
                .plainPassword(new PlainPassword(request.password()))
                .maxParticipantCount(request.maxParticipantCount())
                .description(new Description(request.description()))
                .build();

        Team updatedTeam = teamDataService.overwriteTeam(team, teamDTO);
        teamImageDataService.overwriteImageUrls(updatedTeam, request.imageUrls());
    }

    @Transactional
    public void deleteTeam(MemberInfoDTO memberInfoDTO, Long teamId) {
        Team team = teamDataService.getTeamEntityById(teamId);

        assertTeamLeader(memberInfoDTO, team);

        teamImageDataService.deleteBoardImageUrls(teamId);
        teamMemberDataService.deleteAllTeamMemberForTeamDelete(teamId);
        teamHeartDataService.deleteHeartsByTeamDelete(teamId);
        teamDataService.deleteTeam(teamId);
        //todo: 채팅 구현하면 채팅 삭제 호출 구현하기
    }

    /*
     * <heartDataService 만 Member id가 아닌 Member 엔티티를 파라미터로 준 이유>
     * DataService 는 CRUD 만 하는것이, 권한 검사는 그 위 서비스에서 하는 것이 바람직하다고 생각
     * 그래서 권한 검사를 하려면 Member 엔티티가 필요했음
     */

    @Transactional
    public void createHeart(MemberInfoDTO memberInfoDTO, Long teamId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Team team = teamDataService.getTeamEntityById(teamId);

        teamHeartDataService.createHeart(member, team, teamId);
    }

    @Transactional
    public void deleteHeart(MemberInfoDTO memberInfoDTO, Long teamId) {
        Member member = memberService.getMemberEntityById(memberInfoDTO.id());
        Team team = teamDataService.getTeamEntityById(teamId);

        teamHeartDataService.deleteHeart(member, team, teamId);
    }

    @Transactional
    public void joinTeam(MemberInfoDTO memberInfoDTO, Long teamId) {
        Team team = teamDataService.getTeamEntityById(teamId);
        team.getParticipant().incrementCurrent();
        teamMemberDataService.addTeamMember(team, memberInfoDTO.id());
    }

    @Transactional
    public void delegateTeamLeader(MemberInfoDTO memberInfoDTO, Long teamId,
            TeamLeaderDelegateRequest request) {
        Team team = teamDataService.getTeamEntityById(teamId);
        assertTeamLeader(memberInfoDTO, team);

        if(team.getLeaderId().equals(request.targetMemberId())) {
            throw new TeamLeaderSelfDelegatingException();
        }

        if(!teamMemberDataService.existsTeamMember(teamId, request.targetMemberId())) {
            throw new TeamMemberNotFoundException();
        }

        team.delegateLeader(request.targetMemberId());
    }

    @Transactional
    public void leaveTeam(MemberInfoDTO memberInfoDTO, Long teamId) {
        Team team = teamDataService.getTeamEntityById(teamId);
        if (Objects.equals(team.getLeaderId(), memberInfoDTO.id())) {
            //니 팀 버려? ㅋㅋㅋ
            throw new TeamLeaderCannotLeaveException();
        }

        team.getParticipant().decrementCurrent();
        teamMemberDataService.deleteTeamMember(teamId, memberInfoDTO.id());
    }

    private Boolean isPublic(Team team) {
        return team.getEncryptedPassword() == null;
    }

    private Boolean isJoinable(Team team) {
        return team.getParticipant().getCurrent() < team.getParticipant().getCapacity();
    }

    private void assertTeamLeader(MemberInfoDTO memberInfoDTO, Team team) {
        if (!team.getLeaderId().equals(memberInfoDTO.id())) {
            throw new YouAreNotTeamLeaderException();
        }
    }
}
