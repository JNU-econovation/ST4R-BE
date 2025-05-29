package star.team.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.team.dto.request.TeamRequest;
import star.team.service.TeamService;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;

    @PostMapping
    public ResponseEntity<CommonResponse> createTeam(
            @AuthenticationPrincipal StarUserDetails userDetails,
            TeamRequest request
    ) {
        Long teamId = service.createTeam(userDetails.getMemberInfoDTO(), request);
        URI location = URI.create("/groups/" + teamId);

        return ResponseEntity
                .created(location)
                .body(CommonResponse.success());
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<CommonResponse> updateTeam(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long teamId,
            TeamRequest request
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
