package star.common.infra.websocket.exception;

import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompCommand;
import star.common.exception.client.BadDataSyntaxException;

@Getter
public enum StompMessageType {
    BROADCAST(StompCommand.SEND),
    SUBSCRIBE(StompCommand.SUBSCRIBE);

    StompMessageType(StompCommand messageType) {
        this.messageType = messageType;
    }
    public static StompMessageType fromCode(StompCommand command) {
        for (StompMessageType type : StompMessageType.values()) {
            if (type.getMessageType() == command) {
                return type;
            }
        }
        throw new BadDataSyntaxException("일치하는 메세지 타입이 없습니다.");
    }

    private final StompCommand messageType;
}