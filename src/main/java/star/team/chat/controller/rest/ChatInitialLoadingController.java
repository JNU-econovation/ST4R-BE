package star.team.chat.controller.rest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.security.dto.StarUserDetails;
import star.team.chat.dto.response.ChatPreviewResponse;
import star.team.chat.dto.response.ChatReadResponse;
import star.team.chat.service.ChatCoordinateService;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class ChatInitialLoadingController {

    private final ChatCoordinateService service;

    @GetMapping("/chats/preview")
    public ResponseEntity<List<ChatPreviewResponse>> getPreviewForInitialLoading(
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                service.getPreviewForInitialLoading(userDetails.getMemberInfoDTO())
        );
    }

    @GetMapping("/{teamId}/chats/lastReadTimes")
    public ResponseEntity<List<ChatReadResponse>> getLastReadTimesForInitialLoading(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                service.getLastReadTimesForInitialLoading(teamId, userDetails.getMemberInfoDTO())
        );
    }
}

