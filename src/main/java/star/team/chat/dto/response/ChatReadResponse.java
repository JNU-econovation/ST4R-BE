package star.team.chat.dto.response;

import lombok.Builder;
import org.springframework.lang.Nullable;
import star.team.chat.dto.ChatDTO;

@Builder
public record ChatReadResponse(

        @Nullable
        Long chatDbId,

        Long chatRedisId,
        Integer readCount
) {
        public static ChatReadResponse from(ChatDTO chatDTO, Integer count) {
                return ChatReadResponse.builder()
                        .chatDbId(chatDTO.chatDbId())
                        .chatRedisId(chatDTO.chatRedisId())
                        .readCount(count)
                        .build();
        }
}
