package star.team.chat.controller.rest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.annotation.ResolvePageable;
import star.common.constants.SortField;
import star.common.security.dto.StarUserDetails;
import star.team.chat.dto.response.ChatReadResponse;
import star.team.chat.dto.response.UnreadChatCountsResponse;
import star.team.chat.service.ChatCoordinateService;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class ChatReadController {

    private final ChatCoordinateService service;

    @GetMapping("/chats/unreadCounts")
    public ResponseEntity<List<UnreadChatCountsResponse>> getUnreadCounts(
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(service.getUnreadChatCounts(userDetails.getMemberInfoDTO()));
    }

    @GetMapping("/{teamId}/chats/readCounts")
    public ResponseEntity<Slice<ChatReadResponse>> getReadCounts(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = {SortField.CREATED_AT}) Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.getReadCountsByRedisOrDb(teamId, userDetails.getMemberInfoDTO(), pageable)
        );
    }

    @PostMapping("/{teamId}/chats/markAsRead")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.markAsRead(teamId, userDetails.getMemberInfoDTO());
        return ResponseEntity.noContent().build();
    }
}

