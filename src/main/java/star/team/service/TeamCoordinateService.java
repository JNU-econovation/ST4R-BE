package star.team.service;

import static star.common.constants.CommonConstants.ANONYMOUS_MEMBER_ID;

import jakarta.annotation.Nullable;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.dto.response.internal.Author;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;
import star.team.dto.request.TeamDTO;
import star.team.dto.request.TeamRequest;
import star.team.dto.response.TeamDetailsResponse;
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

        Long memberId = memberInfoDTO.id();
        TeamDTO teamDTO = TeamDTO.builder()
                .name(new Name(request.name()))
                .location(request.location())
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
                .whenToMeet(team.getWhenToMeet())
                .nowParticipants(team.getParticipant().getCurrent())
                .maxParticipants(team.getParticipant().getCapacity())
                .createdAt(team.getCreatedAt())
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
    }

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
