package star.team.chat.dto.response;

import lombok.Builder;
import org.springframework.lang.Nullable;
import star.team.chat.dto.ChatDTO;

@Builder
public record ChatReadResponse(

        @Nullable
        Long chatId,
        Integer readCount
) {
        public static ChatReadResponse from(ChatDTO chatDTO, Integer count) {
                return ChatReadResponse.builder()
                        .chatId(chatDTO.chatId())
                        .readCount(count)
                        .build();
        }
}
