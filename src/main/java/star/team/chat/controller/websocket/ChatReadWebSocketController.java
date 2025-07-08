package star.team.chat.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.team.chat.service.ChatCoordinateService;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatReadWebSocketController {

    private final ChatCoordinateService service;

    @MessageMapping("/read/{teamId}")
    public CommonResponse receiveChatRead(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @DestinationVariable Long teamId
    ) {
        service.markAsRead(teamId, userDetails.getMemberInfoDTO());

        //todo: for debugging
        log.info("{}이(가) 채팅을 읽었습니다 teamId={}", userDetails.getMemberInfoDTO(), teamId);

        return CommonResponse.success();
    }

}
