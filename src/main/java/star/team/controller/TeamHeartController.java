package star.team.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.team.service.TeamCoordinateService;

@RestController
@RequestMapping("groups/{teamId}/likes")
@RequiredArgsConstructor
public class TeamHeartController {

    public final TeamCoordinateService service;

    @PostMapping
    public ResponseEntity<CommonResponse> createHeart(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.createHeart(userDetails.getMemberInfoDTO(), teamId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse> deleteHeart(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.deleteHeart(userDetails.getMemberInfoDTO(), teamId);
        return ResponseEntity.noContent().build();
    }

}
