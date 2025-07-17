package star.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.auth.kakao.dto.KakaoMemberWithdrawDTO;
import star.common.auth.kakao.service.KakaoAuthService;
import star.member.dto.MemberInfoDTO;
import star.member.exception.TeamLeaderCannotWithdrawException;
import star.team.model.entity.Team;
import star.team.service.TeamCoordinateService;
import star.team.service.internal.TeamMemberDataService;

@Service
@RequiredArgsConstructor
public class MemberWithdrawService {

    private final TeamMemberDataService teamMemberDataService;
    private final MemberService memberService;
    private final KakaoAuthService kakaoAuthService;
    private final TeamCoordinateService teamService;

    @Transactional
    public void withdraw(MemberInfoDTO memberInfoDTO) {
        List<Team> allMyTeams = teamMemberDataService.getTeamsByMemberId(memberInfoDTO.id());

        List<String> allLeaderTeamNames = allMyTeams.stream()
                .filter(team -> team.getLeader().getId().equals(memberInfoDTO.id()))
                .map(team -> team.getName().getValue())
                .toList();

        if(!allLeaderTeamNames.isEmpty()) {
            throw new TeamLeaderCannotWithdrawException(allLeaderTeamNames);
        }

        allMyTeams.forEach(team -> teamService.leaveTeam(memberInfoDTO, team.getId()));

        String kakaoAccessToken = memberService.withdraw(memberInfoDTO.id());

        if(kakaoAccessToken != null) {
            kakaoAuthService.unlink(new KakaoMemberWithdrawDTO(kakaoAccessToken));
        }
    }

}
