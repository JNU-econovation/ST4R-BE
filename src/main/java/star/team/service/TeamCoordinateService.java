package star.team.service;

import static star.common.constants.CommonConstants.ANONYMOUS_MEMBER_ID;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.dto.LocalDateTimesDTO;
import star.common.util.CommonTimeUtils;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Member;
import star.member.service.MemberService;
import star.team.annotation.AssertTeamLeader;
import star.team.annotation.AssertTeamMember;
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
import star.team.exception.YouAlreadyJoinedTeamException;
import star.team.exception.YouAreBannedException;
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
    private final ChatCoordinateService chatCoordinateService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createTeam(MemberInfoDTO memberInfoDTO, CreateTeamRequest request) {

        //todo: 인당 만들 수 있는 & 들어갈 수 있는 팀 개수 제한시키기

        Member leader = memberService.getMemberEntityById(memberInfoDTO.id());

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

        Team createdTeam = teamDataService.createTeam(leader, createTeamDTO);

        teamImageDataService.addImageUrls(createdTeam, request.imageUrls());

        TeamMember createdTeamMember = TeamMember.builder()
                .team(createdTeam)
                .member(leader)
                .build();

        teamMemberDataService.addTeamMember(createdTeamMember);

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
                        team ->
                                GetTeamsResponse.from(
                                        team,
                                        teamImageDataService.getImageUrls(team.getId()),
                                        teamHeartDataService.hasHearted(viewerId, team.getId()),
                                        isPublic(team),
                                        existsRealTeamMember(team.getId(), viewerId),
                                        existsBannedTeamMember(team.getId(), viewerId),
                                        isFull(team),
                                        isJoinable(team, viewerId)
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
                .name(request.name())
                .includePast(request.includePast() != null && request.includePast())
                .build();
    }


    @Transactional(readOnly = true)
    public TeamDetailsResponse getTeamDetails(Long teamId, @Nullable MemberInfoDTO memberInfoDTO) {
        Long viewerId = (memberInfoDTO != null) ? memberInfoDTO.id() : ANONYMOUS_MEMBER_ID;
        Team team = teamDataService.getTeamEntityById(teamId);
        MemberInfoDTO authorInfo = MemberInfoDTO.from(team.getLeader());

        return TeamDetailsResponse.from(
                team,
                authorInfo.toAuthor(),
                viewerId.equals(authorInfo.id()),
                teamImageDataService.getImageUrls(teamId),
                teamHeartDataService.hasHearted(viewerId, teamId),
                isPublic(team),
                isJoinable(team, viewerId),
                existsBannedTeamMember(teamId, viewerId),
                isFull(team),
                existsRealTeamMember(teamId, viewerId)
        );
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
    @AssertTeamLeader(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public void updateTeam(MemberInfoDTO memberInfoDTO, Long teamId, UpdateTeamRequest request) {
        Team team = teamDataService.getTeamEntityById(teamId);

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
    @AssertTeamLeader(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public void deleteTeam(MemberInfoDTO memberInfoDTO, Long teamId) {
        teamImageDataService.deleteBoardImageUrls(teamId);
        chatCoordinateService.deleteChats(teamId);
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

    @Transactional
    public void joinTeam(
            MemberInfoDTO memberInfoDTO, Long teamId, @Nullable JoinTeamRequest request
    ) {
        Optional<TeamMember> teamMember = teamMemberDataService.getOptionalTeamMemberEntityByIds(
                teamId, memberInfoDTO.id());

        if (teamMember.isPresent()) {
            TeamMember teamMemberEntity = teamMember.get();
            Team team = teamMemberEntity.getTeam();

            if (teamMemberEntity.getIsBanned()) {
                throw new YouAreBannedException();
            }

            if (!teamMemberEntity.isDeprecated()) {
                throw new YouAlreadyJoinedTeamException();
            }

            matchPassword(team, request == null ? null : request.password());
            teamMemberEntity.markAsActivated();
            team.getParticipant().incrementCurrent();

            return;
        }

        Team team = teamDataService.getTeamEntityById(teamId);
        matchPassword(team, request == null ? null : request.password());
        team.getParticipant().incrementCurrent();

        teamMemberDataService.addTeamMember(
                TeamMember.builder()
                        .team(team)
                        .member(memberService.getMemberEntityById(memberInfoDTO.id()))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    @AssertTeamMember(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public List<TeamMembersResponse> getTeamMembers(Long teamId, MemberInfoDTO memberInfoDTO) {
        List<TeamMember> teamMembers = teamMemberDataService.getTeamMemberEntitiesByTeamId(teamId);

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
    @AssertTeamLeader(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public List<TeamMembersResponse> getBannedTeamMembers(Long teamId,
            MemberInfoDTO memberInfoDTO) {

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
    @AssertTeamLeader(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public void delegateTeamLeader(MemberInfoDTO memberInfoDTO, Long teamId,
            TeamLeaderDelegateRequest request) {

        Team team = teamDataService.getTeamEntityById(teamId);

        Member target = memberService.getMemberEntityById(request.targetMemberId());

        if (team.getLeader().equals(target)) {
            throw new TeamLeaderSelfDelegatingException();
        }

        if (!existsRealTeamMember(teamId, target.getId())) {
            throw new TeamMemberNotFoundException();
        }

        team.delegateLeader(target);
    }

    @Transactional
    @AssertTeamMember(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public void leaveTeam(MemberInfoDTO memberInfoDTO, Long teamId) {

        Team team = teamDataService.getTeamEntityById(teamId);
        if (Objects.equals(team.getLeader().getId(), memberInfoDTO.id())) {
            throw new TeamLeaderCannotLeaveException();
        }

        TeamMember teamMember = teamMemberDataService
                .getOptionalTeamMemberEntityByIds(teamId, memberInfoDTO.id())
                .orElseThrow(TeamMemberNotFoundException::new);

        teamMemberDataService.softDeleteTeamMember(teamMember);
        team.getParticipant().decrementCurrent();
    }

    @Transactional
    @AssertTeamLeader(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public void banTeamMember(MemberInfoDTO memberInfoDTO, Long teamId, Long targetMemberId) {
        if (Objects.equals(targetMemberId, memberInfoDTO.id())) {
            throw new CanNotBanSelfException();
        }

        TeamMember teamMember = teamMemberDataService
                .getOptionalTeamMemberEntityByIds(teamId, targetMemberId)
                .orElseThrow(TeamMemberNotFoundException::new);

        teamMember.ban();
        teamMember.getTeam().getParticipant().decrementCurrent();
    }

    @Transactional
    @AssertTeamMember(memberInfo = "#memberInfoDTO", teamId = "#teamId")
    public void unbanTeamMember(
            MemberInfoDTO memberInfoDTO, Long teamId, TeamMemberUnbanRequest request
    ) {
        Long targetMemberId = request.targetMemberId();

        if (!existsBannedTeamMember(teamId, targetMemberId)) {
            throw new TargetIsNotBannedException();
        }

        teamMemberDataService
                .getOptionalTeamMemberEntityByIds(teamId, targetMemberId)
                .orElseThrow(TeamMemberNotFoundException::new)
                .unban();
    }

    @Transactional(readOnly = true)
    public Boolean isJoinable(Team team, Long memberId) {
        boolean joined = existsRealTeamMember(team.getId(), memberId);
        boolean banned = existsBannedTeamMember(team.getId(), memberId);

        return !joined && !banned && !isFull(team);
    }

    @Transactional(readOnly = true)
    public Boolean isPublic(Team team) {
        return team.getEncryptedPassword() == null;
    }

    @Transactional(readOnly = true)
    public boolean existsRealTeamMember(Long teamId, Long memberId) {
        return teamMemberDataService.existsRealTeamMember(teamId, memberId);
    }

    @Transactional(readOnly = true)
    public boolean existsBannedTeamMember(Long teamId, Long memberId) {
        return teamMemberDataService.existsByBannedTeamMember(teamId, memberId);
    }

    @Transactional(readOnly = true)
    public Boolean isFull(Team team) {
        return team.getParticipant().getCurrent() >= team.getParticipant().getCapacity();
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
