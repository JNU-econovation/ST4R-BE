package star.team.chat.dto;

import java.time.OffsetDateTime;
import lombok.Builder;
import star.team.chat.dto.interfaces.ChatMessage;

@Builder
public record UpdateReadTimeMessageDTO(
        Long teamId,
        Long updateMemberId,
        OffsetDateTime updateReadTime
) implements ChatMessage {

}