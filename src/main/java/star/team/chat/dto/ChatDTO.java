package star.team.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import star.member.dto.MemberInfoDTO;
import star.team.chat.model.entity.Chat;
import star.team.chat.model.vo.Message;

@Builder
public record ChatDTO(
        Long chatId, Long teamId, MemberInfoDTO memberInfo, LocalDateTime chattedAt, Message message
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


}
