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

@Component
public class Rest403Handler implements AccessDeniedHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
                response.getWriter(), CommonResponse.failure(ErrorCode.FORBIDDEN_ERROR,
                        accessDeniedException.getMessage())
        );
    }
}
