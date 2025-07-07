package star.team.chat.exception;

import java.util.List;
import lombok.Getter;
import star.common.exception.server.InternalServerException;
import star.team.chat.dto.ChatDTO;

@Getter
public class RedisRangeExceededException extends InternalServerException {
    private static final String ERROR_MESSAGE = "Redis 범위를 초과했습니다.";

    private final List<ChatDTO> chatMessages;

    public RedisRangeExceededException(List<ChatDTO> chatMessages) {
        super(ERROR_MESSAGE);
        this.chatMessages = chatMessages;
    }
}
