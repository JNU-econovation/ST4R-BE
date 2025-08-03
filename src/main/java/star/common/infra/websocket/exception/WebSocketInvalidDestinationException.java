package star.common.infra.websocket.exception;

import star.common.exception.ErrorCode;
import star.common.exception.client.ClientException;
import star.common.infra.websocket.exception.interfaces.WebSocketException;

public class WebSocketInvalidDestinationException extends ClientException implements
        WebSocketException {

    private final ErrorCode errorCode;
    private final StompMessageType messageType;

    public WebSocketInvalidDestinationException(StompMessageType messageType) {
        super(ErrorCode.WEBSOCKET_INVALID_DESTINATION);
        this.errorCode = ErrorCode.WEBSOCKET_INVALID_DESTINATION;
        this.messageType = messageType;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage().formatted(messageType.name());
    }
}