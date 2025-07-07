package star.team.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.lang.Nullable;
import star.member.dto.MemberInfoDTO;
import star.team.chat.model.entity.Chat;
import star.team.chat.model.vo.Message;

@Builder
public record ChatDTO(
        @Nullable
        Long chatDbId,

        @Nullable
        Long chatRedisId,

        Long teamId,
        MemberInfoDTO memberInfo,
        LocalDateTime chattedAt,
        Message message
) {

    public static ChatDTO from(Chat chat) {
        return ChatDTO.builder()
                .chatDbId(chat.getId())
                .chatRedisId(chat.getRedisId())
                .teamId(chat.getTeamMember().getTeam().getId())
                .memberInfo(MemberInfoDTO.from(chat.getTeamMember().getMember()))
                .chattedAt(chat.getCreatedAt())
                .message(chat.getMessage())
                .build();
    }

    public static ChatDTO from(ChatDTO chat, Long chatRedisId) {
        return ChatDTO.builder()
                .chatDbId(chat.chatDbId())
                .chatRedisId(chatRedisId)
                .teamId(chat.teamId())
                .memberInfo(chat.memberInfo())
                .chattedAt(chat.chattedAt())
                .message(chat.message())
                .build();
    }


}
