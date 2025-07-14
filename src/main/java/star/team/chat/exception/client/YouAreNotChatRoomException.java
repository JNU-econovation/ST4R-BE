package star.team.chat.exception.client;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;
import star.team.chat.exception.ChatException;

public class YouAreNotChatRoomException extends ClientException implements ChatException {

    public YouAreNotChatRoomException() {
        super(ErrorCode.YOU_ARE_NOT_IN_CHAT_ROOM);
    }
}
