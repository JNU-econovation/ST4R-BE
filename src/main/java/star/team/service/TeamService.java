package star.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.team.dto.request.TeamDTO;
import star.team.dto.request.TeamRequest;
import star.team.model.entity.Team;
import star.team.model.vo.Description;
import star.team.model.vo.Name;
import star.team.model.vo.PlainPassword;
import star.team.service.internal.TeamDataService;
import star.team.service.internal.TeamImageDataService;
import star.team.service.internal.TeamMemberDataService;

@Service
@RequiredArgsConstructor
public class TeamService {

    private TeamDataService teamDataService;
    private TeamImageDataService teamImageDataService;
    private TeamMemberDataService teamMemberDataService;

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

}
