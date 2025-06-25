package star.team.chat.exception.handler;

import star.common.exception.client.Client403Exception;

public class YouAreNotChatRoomException extends Client403Exception {
    private static final String ERROR_MESSAGE = "유효하지 않은 채팅방 입니다.";

    public YouAreNotChatRoomException() {
        super(ERROR_MESSAGE);
    }
}
