package star.team.chat.dto.broadcast;

import lombok.Builder;
import star.team.chat.dto.interfaces.ChatMessage;
import star.team.chat.enums.MessageType;

@Builder
public record ChatBroadcast(
        String messageType,
        ChatMessage message
) {
    public static ChatBroadcast from(MessageType messageType, ChatMessage message) {

        return ChatBroadcast.builder()
                .messageType(messageType.getTypeString())
                .message(message)
                .build();
    }
}