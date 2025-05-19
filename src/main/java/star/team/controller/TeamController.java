package star.team.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

}
