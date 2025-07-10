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
import star.team.chat.dto.response.ChatPreviewResponse;
import star.team.chat.dto.response.ChatReadResponse;
import star.team.chat.service.ChatCoordinateService;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class ChatReadController {

    private final ChatCoordinateService service;

    /**
     * [최초 로딩용] 사용자가 속한 모든 팀의 채팅방 프리뷰 정보를 조회합니다.
     * 이 API는 채팅방 목록 화면에 처음 진입할 때 한 번만 호출되어야 합니다.
     * 이후의 모든 업데이트는 WebSocket PUSH를 통해 실시간으로 이루어집니다.
     */
    @GetMapping("/chats/preview")
    public ResponseEntity<List<ChatPreviewResponse>> getPreview(
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(service.       getPreview(userDetails.getMemberInfoDTO()));
    }

    @GetMapping("/{teamId}/chats/readCounts")
    public ResponseEntity<Slice<ChatReadResponse>> getReadCounts(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = {SortField.CHATTED_AT}) Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.getReadCounts(teamId, userDetails.getMemberInfoDTO(), pageable)
        );
    }
    //todo: 채팅 구독할때 -> 채팅을 읽는다 고민을 해보기
    /*
     *
     *
     *
     *
     */

    @PostMapping("/{teamId}/chats/markAsRead")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long teamId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.markAsRead(teamId, userDetails.getMemberInfoDTO());
        return ResponseEntity.noContent().build();
    }
}

