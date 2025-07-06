package star.team.service.internal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.service.MemberService;
import star.team.model.entity.Team;
import star.team.model.entity.TeamMember;
import star.team.repository.TeamMemberRepository;

@Service
@RequiredArgsConstructor
public class TeamMemberDataService {

    private final MemberService memberService;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void addTeamMember(Team team, Long memberId) {

        if (teamMemberRepository.existsByTeamIdAndMemberId(team.getId(), memberId)) {
            TeamMember teamMember = getTeamMemberEntityByIds(team.getId(), memberId);

            teamMember.markAsActivated();

            return;
        }

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .member(memberService.getMemberEntityById(memberId))
                .build();

        teamMemberRepository.save(teamMember);
    }

    @Transactional(readOnly = true)
    public TeamMember getTeamMemberEntityByIds(Long teamId, Long memberId) {
        return teamMemberRepository.getByTeamIdAndMemberId(teamId, memberId);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getBannedTeamMemberEntitiesByTeamId(Long teamId) {
        return teamMemberRepository.getByTeamIdAndIsBanned(teamId, true);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getTeamMemberEntitiesByTeamId(Long teamId) {
        return teamMemberRepository.getByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public boolean existsTeamMember(Long teamId, Long memberId) {
        return teamMemberRepository.existsByTeamIdAndMemberId(teamId, memberId);
    }

    @Transactional
    public void banTeamMember(Long teamId, Long memberId) {
        TeamMember teamMember = getTeamMemberEntityByIds(teamId, memberId);
        teamMember.ban();
    }

    @Transactional
    public void unbanTeamMember(Long teamId, Long memberId) {
        TeamMember teamMember = getTeamMemberEntityByIds(teamId, memberId);
        teamMember.unban();
    }

    @Transactional
    public void softDeleteTeamMember(Long teamId, Long memberId) {
        TeamMember teamMember = getTeamMemberEntityByIds(teamId, memberId);
        teamMember.markAsDeprecated();
    }

    @Transactional
    public void deleteAllTeamMemberForTeamDelete(Long teamId) {
        teamMemberRepository.deleteTeamMembersByTeamId(teamId);
    }
}
