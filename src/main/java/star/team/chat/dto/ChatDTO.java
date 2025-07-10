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
        Long chatId,

        Long teamId,
        MemberInfoDTO memberInfo,
        LocalDateTime chattedAt,
        Message message
) {

    public static ChatDTO from(Chat chat) {
        return ChatDTO.builder()
                .chatId(chat.getId())
                .teamId(chat.getTeamMember().getTeam().getId())
                .memberInfo(MemberInfoDTO.from(chat.getTeamMember().getMember()))
                .chattedAt(chat.getCreatedAt())
                .message(chat.getMessage())
                .build();
    }

    public static ChatDTO from(ChatDTO chat) {
        return ChatDTO.builder()
                .chatId(chat.chatId())
                .teamId(chat.teamId())
                .memberInfo(chat.memberInfo())
                .chattedAt(chat.chattedAt())
                .message(chat.message())
                .build();
    }


}
