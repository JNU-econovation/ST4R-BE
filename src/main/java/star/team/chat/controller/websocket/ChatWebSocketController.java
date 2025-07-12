package star.team.chat.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import star.common.security.dto.StarUserDetails;
import star.team.chat.dto.send.ChatSend;
import star.team.chat.service.ChatCoordinateService;


@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatCoordinateService service;

    @MessageMapping("/broadcast/{teamId}")
    public void receiveMessage(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @DestinationVariable Long teamId,
            ChatSend chat
    ) {

        //todo: for debugging
        log.info("receiveMessage: teamId={}, chat={}", teamId, chat);

        service.publishAndSaveChat(teamId, chat, userDetails.getMemberInfoDTO());
    }

    @MessageMapping("/markAsRead/{teamId}")
    public void markAsRead(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @DestinationVariable Long teamId
    ) {

        //todo: for debugging
        log.info("markAsRead: teamId={}, memberInfo={}", teamId, userDetails.getMemberInfoDTO());

        service.markAsReadAndPublishUpdatedReadTime(teamId, userDetails.getMemberInfoDTO());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("새로운 웹소켓 연결이 생성되었습니다!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("웹소켓 연결이 종료되었습니다!");
    }

}
