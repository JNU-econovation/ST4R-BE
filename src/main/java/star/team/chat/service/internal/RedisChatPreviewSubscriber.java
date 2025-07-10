package star.team.chat.service.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import star.team.chat.dto.response.ChatPreviewResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisChatPreviewSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public void onPreviewUpdate(String publishedMessage) {
        try {
            ChatPreviewResponse previewResponse = objectMapper.readValue(publishedMessage,
                    ChatPreviewResponse.class);

            messagingTemplate.convertAndSendToUser(
                    previewResponse.targetMemberId().toString(),
                    "/queue/previews",
                    previewResponse
            );
        } catch (Exception e) {
            log.error("채팅 메시지 미리보기 처리 중 오류 발생", e);
        }
    }
}
