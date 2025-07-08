package star.team.chat.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.annotation.ResolvePageable;
import star.common.constants.SortField;
import star.common.security.dto.StarUserDetails;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.service.ChatCoordinateService;

@RestController
@RequestMapping("groups/{teamId}/chats")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatCoordinateService service;

    @GetMapping
    public ResponseEntity<Slice<ChatResponse>> getChatHistory(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = {SortField.CHATTED_AT}) Pageable pageable
    ) {

        return ResponseEntity.ok(
                service.getChatHistory(teamId, userDetails.getMemberInfoDTO(), pageable)
        );
    }
}