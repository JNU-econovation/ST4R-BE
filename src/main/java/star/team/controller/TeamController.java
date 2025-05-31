package star.team.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.annotation.ResolvePageable;
import star.common.constants.SortField;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.member.dto.MemberInfoDTO;
import star.team.dto.request.TeamRequest;
import star.team.dto.response.TeamDetailsResponse;
import star.team.dto.response.GetTeamsResponse;
import star.team.service.TeamCoordinateService;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class TeamController {

    private final TeamCoordinateService service;

    @PostMapping
    public ResponseEntity<CommonResponse> createTeam(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid @RequestBody TeamRequest request
    ) {
        Long teamId = service.createTeam(userDetails.getMemberInfoDTO(), request);
        URI location = URI.create("/groups/" + teamId);

        return ResponseEntity.created(location).body(CommonResponse.success());
    }

    @GetMapping
    public ResponseEntity<Page<GetTeamsResponse>> getTeams(
            @Nullable @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = {SortField.CREATED_AT, SortField.WHEN_TO_MEET,
                    SortField.HEART_COUNT})
            Pageable pageable
    ) {
        MemberInfoDTO memberInfoDTO = (userDetails != null) ? userDetails.getMemberInfoDTO() : null;

        return ResponseEntity.ok(service.getTeams(memberInfoDTO, pageable));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailsResponse> getTeamDetails(
            @Nullable @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long teamId
    ) {
        MemberInfoDTO memberInfoDTO = (userDetails != null) ? userDetails.getMemberInfoDTO() : null;

        return ResponseEntity.ok(service.getTeamDetails(teamId, memberInfoDTO));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<CommonResponse> updateTeam(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long teamId,
            @Valid @RequestBody TeamRequest request
    ) {
        service.updateTeam(userDetails.getMemberInfoDTO(), teamId, request);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long teamId
    ) {
        service.deleteTeam(userDetails.getMemberInfoDTO(), teamId);

        return ResponseEntity.noContent().build();
    }

}
