package star.team.chat.service.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import star.team.chat.dto.broadcast.ChatBroadcast;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public void onChatMessage(String publishedMessage) {
        try {
            ChatBroadcast chatBroadcast = objectMapper.readValue(publishedMessage,
                    ChatBroadcast.class);

            messagingTemplate.convertAndSend("/subscribe/" + chatBroadcast.chatMessage().teamId(),
                    chatBroadcast);

            log.info("Redis Pub/Sub 메시지 수신 및 전송 완료: teamId={}, message={}",
                    chatBroadcast.chatMessage().teamId(),
                    chatBroadcast.chatMessage());

        } catch (Exception e) {
            log.error("채팅 메시지 처리 중 오류 발생", e);
        }
    }
}
