package star.common.security.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import star.common.dto.response.CommonResponse;
import star.common.exception.ErrorCode;
import star.common.security.exception.CustomAccessDeniedException;

@Component
public class Rest403Handler implements AccessDeniedHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        ErrorCode errorCode = ErrorCode.UNKNOWN_FORBIDDEN_ERROR;

        if (accessDeniedException instanceof CustomAccessDeniedException customEx) {
            errorCode = customEx.getErrorCode();
        }

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
                response.getWriter(),
                CommonResponse.failure(errorCode, errorCode.getMessage())
        );
    }
}
