package star.team.service.internal;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.team.model.entity.Team;
import star.team.model.entity.TeamMember;
import star.team.repository.TeamMemberRepository;

@Service
@RequiredArgsConstructor
public class TeamMemberDataService {

    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void addTeamMember(TeamMember teamMember) {
        teamMemberRepository.save(teamMember);
    }

    @Transactional(readOnly = true)
    public Optional<TeamMember> getOptionalTeamMemberEntityByIds(Long teamId, Long memberId) {
        return teamMemberRepository.getOptionalTeamMemberByTeamIdAndMemberId(teamId, memberId);
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
    public List<Long> getAllMemberIdInTeam(Long teamId) {
        return teamMemberRepository.getAllMemberIdInTeam(teamId);
    }

    @Transactional(readOnly = true)
    public List<Team> getTeamsByMemberId(Long memberId) {
        return teamMemberRepository.getTeamsByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllTeamIdByMemberId(Long memberId) {
        return teamMemberRepository.getTeamIdsByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public boolean existsRealTeamMember(Long teamId, Long memberId) {
        return teamMemberRepository.existsTeamMember(teamId, memberId, false, false);
    }

    @Transactional(readOnly = true)
    public boolean existsByBannedTeamMember(Long teamId, Long memberId) {
        return teamMemberRepository.existsTeamMember(teamId, memberId, true);
    }

    @Transactional
    public void softDeleteTeamMember(TeamMember teamMember) {
        teamMember.markAsDeprecated();
    }

    @Transactional
    public void deleteAllTeamMemberForTeamDelete(Long teamId) {
        teamMemberRepository.deleteTeamMembersByTeamId(teamId);
    }
}
