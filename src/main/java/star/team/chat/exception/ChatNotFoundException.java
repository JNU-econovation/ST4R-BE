package star.team.chat.exception;

import star.common.exception.server.InternalServerException;
import star.team.chat.dto.ChatDTO;

public class ChatNotFoundException extends InternalServerException {
    private static final String ERROR_MESSAGE = "채팅을 찾을 수 없습니다. -> %s";


    public ChatNotFoundException(ChatDTO chatDTO) {
        super(ERROR_MESSAGE.formatted(chatDTO));
    }
}
