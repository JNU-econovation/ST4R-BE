package star.team.chat.dto.response;

import java.time.OffsetDateTime;

public record ChatResponse(
        Long chatId, Long memberId, String nickName, OffsetDateTime chattedAt, String message
) {

}