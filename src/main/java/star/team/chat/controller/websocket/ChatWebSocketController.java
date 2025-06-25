package star.team.chat.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import star.common.security.dto.StarUserDetails;
import star.team.chat.dto.request.ChatRequest;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.service.ChatService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService service;

    @MessageMapping("/broadcast/{teamId}")
    @SendTo("/subscribe/{teamId}")
    public ChatResponse receiveMessage(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @DestinationVariable("teamId") Long teamId,
            ChatRequest request) {

        //todo: for debugging
        log.info("receiveMessage: teamId={}, request={}", teamId, request);

        return service.saveChat(teamId, request, userDetails.getMemberInfoDTO());
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
