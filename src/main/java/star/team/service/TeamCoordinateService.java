package star.team.service;

import static star.common.constants.CommonConstants.ANONYMOUS_MEMBER_ID;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.dto.LocalDateTimesDTO;
import star.common.dto.internal.Author;
import star.common.util.CommonTimeUtils;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;
import star.team.chat.service.ChatCoordinateService;
import star.team.dto.CreateTeamDTO;
import star.team.dto.TeamSearchDTO;
import star.team.dto.UpdateTeamDTO;
import star.team.dto.request.CreateTeamRequest;
import star.team.dto.request.GetTeamsRequest;
import star.team.dto.request.JoinTeamRequest;
import star.team.dto.request.TeamLeaderDelegateRequest;
import star.team.dto.request.TeamMemberUnbanRequest;
import star.team.dto.request.UpdateTeamRequest;
import star.team.dto.response.GetMyTeamsResponse;
import star.team.dto.response.GetTeamsResponse;
import star.team.dto.response.TeamDetailsResponse;
import star.team.dto.response.TeamMembersResponse;
import star.team.exception.CanNotBanSelfException;
import star.team.exception.InvalidTeamPasswordException;
import star.team.exception.TargetIsNotBannedException;
import star.team.exception.TeamLeaderCannotLeaveException;
import star.team.exception.TeamLeaderSelfDelegatingException;
import star.team.exception.TeamMemberNotFoundException;
import star.team.exception.TeamNotFoundException;
import star.team.exception.YouAlreadyJoinedTeamException;
import star.team.exception.YouAreBannedException;
import star.team.exception.YouAreNotTeamLeaderException;
import star.team.model.entity.Team;
import star.team.model.entity.TeamMember;
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
    private final PasswordEncoder passwordEncoder;
    private final ChatCoordinateService chatCoordinateService;

    @Transactional
    public Long createTeam(MemberInfoDTO memberInfoDTO, CreateTeamRequest request) {

        //todo: 인당 만들 수 있는 & 들어갈 수 있는 팀 개수 제한시키기

        Long memberId = memberInfoDTO.id();
        CreateTeamDTO createTeamDTO = CreateTeamDTO.builder()
                .name(new Name(request.name()))
                .location(request.location())
                .whenToMeet(request.whenToMeet())
                .plainPassword(
                        request.password() == null
                                ? null : PlainPassword.builder().value(request.password()).build()
                )
                .maxParticipantCount(request.maxParticipantCount())
                .description(new Description(request.description()))
                .build();

        Team createdTeam =
                teamDataService.createTeam(memberService.getMemberEntityById(memberId),
                        createTeamDTO);
        teamImageDataService.addImageUrls(createdTeam, request.imageUrls());
        teamMemberDataService.addTeamMember(createdTeam, memberId);

        return createdTeam.getId();
    }

    @Transactional(readOnly = true)
    public Page<GetTeamsResponse> getTeams(@Nullable MemberInfoDTO memberInfoDTO,
            GetTeamsRequest request, Pageable pageable) {
        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;

        TeamSearchDTO searchDTO = buildTeamSearchDTO(request);

        Page<Team> teams = teamDataService.getTeams(searchDTO, pageable);

        List<GetTeamsResponse> getTeamsResponseList = teams.getContent().stream()
                .map(
                        team -> GetTeamsResponse.from(
                                team,
                                teamImageDataService.getImageUrls(team.getId()),
                                teamHeartDataService.hasHearted(viewerId, team.getId()),
                                isJoinable(team, viewerId),
                                isPublic(team)
                        )
                ).toList();

        return new PageImpl<>(getTeamsResponseList, pageable, getTeamsResponseList.size());
    }

    private TeamSearchDTO buildTeamSearchDTO(GetTeamsRequest request) {
        LocalDateTimesDTO meetBetWeen = null;

        if (request.meetBetweenStart() != null && request.meetBetweenEnd() != null) {
            meetBetWeen = LocalDateTimesDTO.builder()
                    .start(
                            CommonTimeUtils.convertOffsetDateTimeToLocalDateTime(
                                    request.meetBetweenStart()
                            )
                    )
                    .end(
                            CommonTimeUtils.convertOffsetDateTimeToLocalDateTime(
                                    request.meetBetweenEnd()
                            )
                    )
                    .build();
        }

        return TeamSearchDTO.builder()
                .circularArea(request.circularArea())
                .meetBetween(meetBetWeen)
                .leaderName(request.leaderName())
                .name(request.name() == null ? null : new Name(request.name()))
                .includePast(request.includePast() != null && request.includePast())
                .build();
    }


    @Transactional(readOnly = true)
    public TeamDetailsResponse getTeamDetails(Long teamId, @Nullable MemberInfoDTO memberInfoDTO) {
        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;
        Team team = teamDataService.getTeamEntityById(teamId);
        MemberInfoDTO authorInfo = MemberInfoDTO.from(team.getLeader());

        Author author = Author.builder()
                .id(authorInfo.id())
                .nickname(authorInfo.email().getValue())
                .imageUrl(authorInfo.profileImageUrl())
                .build();

        return TeamDetailsResponse.from(
                team,
                author,
                viewerId.equals(authorInfo.id()),
                teamImageDataService.getImageUrls(teamId),
                teamHeartDataService.hasHearted(viewerId, teamId),
                isPublic(team),
                isJoinable(team, viewerId)
        );
    }

    @Transactional(readOnly = true)
    public TeamMember getTeamMember(Long teamId, Long memberId) {
        if (!teamMemberDataService.existsTeamMember(teamId, memberId)) {
            throw new TeamMemberNotFoundException();
        }

        return teamMemberDataService.getTeamMemberEntityByIds(teamId, memberId).get();
    }

    @Transactional(readOnly = true)
    public List<GetMyTeamsResponse> getMyTeams(MemberInfoDTO memberInfoDTO) {

        return teamMemberDataService.getTeamsByMemberId(memberInfoDTO.id())
                .stream().map(team -> {
                    List<String> imageUrls = teamImageDataService.getImageUrls(team.getId());
                    return GetMyTeamsResponse.from(
                            team, imageUrls.isEmpty() ? null : imageUrls.getFirst()
                    );
                }).toList();
    }

    @Transactional
    public void updateTeam(MemberInfoDTO memberInfoDTO, Long teamId, UpdateTeamRequest request) {
        Team team = teamDataService.getTeamEntityById(teamId);

        assertTeamLeader(memberInfoDTO, team);

        UpdateTeamDTO updateTeamDTO = UpdateTeamDTO.builder()
                .name(new Name(request.name()))
                .location(request.location())
                .newWhenToMeet(request.changeWhenToMeet() ? request.newWhenToMeet() : null)
                .plainPassword(
                        request.changePassword() ? new PlainPassword(request.newPassword()) : null
                )
                .maxParticipantCount(request.maxParticipantCount())
                .description(new Description(request.description()))
                .build();

        Team updatedTeam = teamDataService.updateTeam(team, updateTeamDTO);
        teamImageDataService.overwriteImageUrls(updatedTeam, request.imageUrls());
    }

    @Transactional
    public void deleteTeam(MemberInfoDTO memberInfoDTO, Long teamId) {
        Team team = teamDataService.getTeamEntityById(teamId);

        assertTeamLeader(memberInfoDTO, team);

        teamImageDataService.deleteBoardImageUrls(teamId);
        chatCoordinateService.deleteChats(teamId);
        teamMemberDataService.deleteAllTeamMemberForTeamDelete(teamId);
        teamHeartDataService.deleteHeartsByTeamDelete(teamId);
        teamDataService.deleteTeam(teamId);
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
    public void joinTeam(MemberInfoDTO memberInfoDTO, Long teamId, JoinTeamRequest request) {

        TeamMember teamMember = getTeamMember(teamId, memberInfoDTO.id());

        if (teamMember != null) {
            if (teamMember.getIsBanned()) {
                throw new YouAreBannedException();
            }

            if (!teamMember.isDeprecated()) {
                throw new YouAlreadyJoinedTeamException();
            }
        }

        Team team = teamDataService.getTeamEntityById(teamId);

        matchPassword(team, request.password());

        team.getParticipant().incrementCurrent();
        teamMemberDataService.addTeamMember(team, memberInfoDTO.id());
    }

    @Transactional(readOnly = true)
    public List<TeamMembersResponse> getTeamMembers(Long teamId, MemberInfoDTO memberInfoDTO) {
        List<TeamMember> teamMembers = teamMemberDataService.getTeamMemberEntitiesByTeamId(teamId);

        if (teamMembers.isEmpty()) {
            throw new TeamNotFoundException();
        }

        Long leaderId = teamMembers.getFirst().getTeam().getLeader().getId();

        return teamMembers.stream()
                .filter(teamMember -> !teamMember.isDeprecated())
                .filter(teamMember -> !teamMember.getIsBanned())
                .map(
                        teamMember -> {
                            Member member = teamMember.getMember();
                            Long memberId = member.getId();

                            return TeamMembersResponse.from(
                                    MemberInfoDTO.from(member),
                                    memberId.equals(leaderId),
                                    memberId.equals(memberInfoDTO.id())
                            );
                        }
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeamMembersResponse> getBannedTeamMembers(Long teamId,
            MemberInfoDTO memberInfoDTO) {

        Team team = teamDataService.getTeamEntityById(teamId);

        assertTeamLeader(memberInfoDTO, team);

        List<TeamMember> bannedTeamMembers = teamMemberDataService.getBannedTeamMemberEntitiesByTeamId(
                teamId);

        return bannedTeamMembers.stream()
                .map(
                        teamMember -> {
                            Member member = teamMember.getMember();

                            return TeamMembersResponse.from(
                                    MemberInfoDTO.from(member),
                                    false,
                                    false
                            );
                        }
                )
                .toList();
    }


    @Transactional
    public void delegateTeamLeader(MemberInfoDTO memberInfoDTO, Long teamId,
            TeamLeaderDelegateRequest request) {

        Team team = teamDataService.getTeamEntityById(teamId);
        assertTeamLeader(memberInfoDTO, team);

        Member target = memberService.getMemberEntityById(request.targetMemberId());

        if (team.getLeader().equals(target)) {
            throw new TeamLeaderSelfDelegatingException();
        }

        if (!teamMemberDataService.existsTeamMember(teamId, target.getId())) {
            throw new TeamMemberNotFoundException();
        }

        team.delegateLeader(target);
    }

    @Transactional
    public void leaveTeam(MemberInfoDTO memberInfoDTO, Long teamId) {

        Team team = teamDataService.getTeamEntityById(teamId);
        assertTeamMember(memberInfoDTO, team.getId());
        if (Objects.equals(team.getLeader().getId(), memberInfoDTO.id())) {
            //니 팀 버려? ㅋㅋㅋ
            throw new TeamLeaderCannotLeaveException();
        }

        team.getParticipant().decrementCurrent();
        teamMemberDataService.softDeleteTeamMember(teamId, memberInfoDTO.id());
    }

    @Transactional
    public void banTeamMember(MemberInfoDTO memberInfoDTO, Long teamId, Long memberId) {
        if (Objects.equals(memberId, memberInfoDTO.id())) {
            throw new CanNotBanSelfException();
        }

        Team team = teamDataService.getTeamEntityById(teamId);
        assertTeamLeader(memberInfoDTO, team);

        teamMemberDataService.banTeamMember(teamId, memberId);
        team.getParticipant().decrementCurrent();
    }

    @Transactional
    public void unbanTeamMember(MemberInfoDTO memberInfoDTO, Long teamId,
            TeamMemberUnbanRequest request) {
        assertTeamLeader(memberInfoDTO, teamDataService.getTeamEntityById(teamId));

        MemberInfoDTO bannedMemberInfo = memberService.getMemberById(request.targetMemberId());

        Team team = teamDataService.getTeamEntityById(teamId);
        assertBanned(bannedMemberInfo, team);

        teamMemberDataService.unbanTeamMember(teamId, request.targetMemberId());
    }

    private Boolean isPublic(Team team) {
        return team.getEncryptedPassword() == null;
    }

    private Boolean isJoinable(Team team, Long memberId) {
        boolean isNotFull =
                team.getParticipant().getCurrent() < team.getParticipant().getCapacity();
        boolean joined = teamMemberDataService.existsTeamMember(team.getId(), memberId);

        return (isNotFull && !joined);
    }

    private void assertTeamLeader(MemberInfoDTO memberInfoDTO, Team team) {
        if (!team.getLeader().getId().equals(memberInfoDTO.id())) {
            throw new YouAreNotTeamLeaderException();
        }
    }

    @Transactional(readOnly = true)
    public void assertTeamMember(MemberInfoDTO memberInfoDTO, Long teamId) {
        if (!teamMemberDataService.existsTeamMember(teamId, memberInfoDTO.id())) {
            throw new TeamMemberNotFoundException();
        }
    }

    private void assertBanned(MemberInfoDTO targetMemberInfo, Team team) {
        if (teamMemberDataService.existsTeamMember(team.getId(), targetMemberInfo.id())) {
            TeamMember teamMember = getTeamMember(team.getId(), targetMemberInfo.id());

            if (!teamMember.getIsBanned()) {
                throw new TargetIsNotBannedException();
            }

            return;
        }

        throw new TeamMemberNotFoundException();
    }

    private void matchPassword(Team team, String password) {
        if (team.getEncryptedPassword() == null) {
            return;
        }

        if (password == null ||
                !passwordEncoder.matches(password, team.getEncryptedPassword().getValue())) {
            throw new InvalidTeamPasswordException();
        }
    }
}
