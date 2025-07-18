package star.team.chat.exception.handler;

import static star.common.constants.CommonConstants.CRITICAL_ERROR_MESSAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import star.common.exception.client.ClientException;

@Controller
@Slf4j
public class ChatExceptionHandler {

    @MessageExceptionHandler(ClientException.class)
    @SendToUser("/queue/errors")
    public String handleClientException(ClientException e) {
        log.warn("웹소켓 클라이언트 관련 에러: {}", e.getMessage());
        return e.getErrorCode().getMessage();
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public String handleException(Exception e) {
        log.warn("{}: {}", CRITICAL_ERROR_MESSAGE, e.getMessage());
        return CRITICAL_ERROR_MESSAGE;
    }

}
