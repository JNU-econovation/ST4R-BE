package star.team.chat.dto;

import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.dto.internal.Author;
import star.common.util.CommonTimeUtils;
import star.team.chat.dto.interfaces.ChatMessage;

@Builder
public record GeneralMessageDTO(
        Long teamId, Long chatId, Author sender, OffsetDateTime chattedAt, String message
) implements ChatMessage {

    public static GeneralMessageDTO from(ChatDTO chat) {
        return GeneralMessageDTO.builder()
                .teamId(chat.teamId())
                .chatId(chat.chatId())
                .sender(chat.memberInfo().toAuthor())
                .chattedAt(CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(chat.chattedAt()))
                .message(chat.message().getValue())
                .build();
    }
}
