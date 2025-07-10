package star.team.chat.exception.server;

import java.util.List;
import lombok.Getter;
import star.common.exception.server.InternalServerException;
import star.team.chat.dto.ChatDTO;

@Getter
public class RedisRangeExceededException extends InternalServerException {
    private static final String ERROR_MESSAGE = "Redis 범위를 초과했습니다.";

    private final List<ChatDTO> redisChats;

    public RedisRangeExceededException(List<ChatDTO> redisChats) {
        super(ERROR_MESSAGE);
        this.redisChats = redisChats;
    }
}
