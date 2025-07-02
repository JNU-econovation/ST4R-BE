package star.team.chat.dto.response;

import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.util.CommonTimeUtils;
import star.team.chat.model.entity.Chat;

@Builder
public record ChatResponse(
        Long chatId, Long memberId, String email, OffsetDateTime chattedAt, String message
) {
    public static ChatResponse from(Chat chat) {
        return ChatResponse.builder()
                .chatId(chat.getId())
                .memberId(chat.getTeamMember().getMember().getId())
                .email(chat.getTeamMember().getMember().getEmail().getValue())
                .chattedAt(CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(chat.getCreatedAt()))
                .message(chat.getMessage().getValue())
                .build();

    }
}