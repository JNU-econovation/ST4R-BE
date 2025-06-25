package star.common.security.interceptor;

import static star.common.security.constants.SecurityConstants.CRITICAL_AUTH_ERROR_MESSAGE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import star.common.security.helper.JwtAuthHelper;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketJwtAuthInterceptor implements ChannelInterceptor {

    private final JwtAuthHelper jwtAuthHelper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
                StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();

        if (command == StompCommand.CONNECT) {
            try {
                List<String> authorizationHeaders = accessor.getNativeHeader(
                        HttpHeaders.AUTHORIZATION);

                if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
                    throw new InsufficientAuthenticationException("Auth 헤더가 유효하지 않습니다.");
                }

                Authentication auth = jwtAuthHelper.authenticate(authorizationHeaders.getFirst());

                accessor.setUser(auth);

            } catch (AuthenticationException ex) {
                log.warn("웹소켓 인증에 실패하였습니다. {}", ex.getMessage());
                return new ErrorMessage(ex);
            } catch (Exception ex) {
                log.error(CRITICAL_AUTH_ERROR_MESSAGE, ex);
                return new ErrorMessage(
                        new AuthenticationServiceException(CRITICAL_AUTH_ERROR_MESSAGE, ex));
            }
        }
        return message;
    }
}