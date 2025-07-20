package star.team.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.security.dto.StarUserDetails;
import star.team.dto.request.JoinTeamRequest;
import star.team.dto.request.TeamLeaderDelegateRequest;
import star.team.dto.request.TeamMemberUnbanRequest;
import star.team.dto.response.TeamMembersResponse;
import star.team.service.TeamCoordinateService;

@RestController
@RequestMapping("/groups/{teamId}/members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamCoordinateService service;

    @PostMapping
    public ResponseEntity<Void> joinTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody(required = false) JoinTeamRequest request
    ) {
        service.joinTeam(userDetails.getMemberInfoDTO(), teamId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TeamMembersResponse>> getTeamMembers(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(service.getTeamMembers(teamId, userDetails.getMemberInfoDTO()));
    }

    @GetMapping("/bans")
    public ResponseEntity<List<TeamMembersResponse>> getBannedTeamMembers(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                service.getBannedTeamMembers(teamId, userDetails.getMemberInfoDTO()));
    }

    @DeleteMapping
    public ResponseEntity<Void> leaveTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.leaveTeam(userDetails.getMemberInfoDTO(), teamId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> banTeamMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.banTeamMember(userDetails.getMemberInfoDTO(), teamId, memberId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/leader")
    public ResponseEntity<Void> delegateTeamLeader(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody TeamLeaderDelegateRequest request
    ) {
        service.delegateTeamLeader(userDetails.getMemberInfoDTO(), teamId, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/bannedMembers")
    public ResponseEntity<Void> unbanTeamMember(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody TeamMemberUnbanRequest request
    ) {
        service.unbanTeamMember(userDetails.getMemberInfoDTO(), teamId, request);

        return ResponseEntity.noContent().build();
    }

}
