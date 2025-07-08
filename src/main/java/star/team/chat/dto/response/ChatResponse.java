package star.team.chat.dto.response;

import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.util.CommonTimeUtils;
import star.member.dto.MemberInfoDTO;
import star.team.chat.dto.ChatDTO;

@Builder
public record ChatResponse(
        @Nullable
        Long chatDbId,

        @Nullable
        Long chatRedisId,

        Long memberId, String email, OffsetDateTime chattedAt, String message
) {
    public static ChatResponse from(ChatDTO chat) {
        MemberInfoDTO memberInfo = chat.memberInfo();

        return ChatResponse.builder()
                .chatDbId(chat.chatDbId())
                .chatRedisId(chat.chatRedisId())
                .memberId(memberInfo.id())
                .email(memberInfo.email().getValue())
                .chattedAt(CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(chat.chattedAt()))
                .message(chat.message().getValue())
                .build();

    }
}