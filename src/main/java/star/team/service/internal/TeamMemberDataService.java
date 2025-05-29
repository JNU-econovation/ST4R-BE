package star.team.service.internal;

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

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .member(memberService.getMemberEntityById(memberId))
                .build();

        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void deleteAllTeamMemberForTeamDelete(Long teamId) {
        teamMemberRepository.deleteTeamMembersByTeamId(teamId);
    }
}
