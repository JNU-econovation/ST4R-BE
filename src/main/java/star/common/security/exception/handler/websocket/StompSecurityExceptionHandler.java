package star.common.security.exception.handler.websocket;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import star.common.exception.ErrorCode;
import star.common.security.exception.CustomAuthenticationException;

@Component
@Slf4j
@NoArgsConstructor
public class StompSecurityExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable cause = ex.getCause();

        return switch (cause) {
            case CustomAuthenticationException e -> {
                log.warn("CustomAuthenticationException이 StompSecurityExceptionHandler 에서 핸들링 됨 -> {}"
                        , e.getMessage());

                yield setErrorMessage(e.getMessage());
            }

            default -> {
                log.error("UnknownException이 StompSecurityExceptionHandler 에서 핸들링 됨 -> {}",
                        cause.getMessage());

                yield setErrorMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
            }
        };

    }

    private Message<byte[]> setErrorMessage(String message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(message);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
