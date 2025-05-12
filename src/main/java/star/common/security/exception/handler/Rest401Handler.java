package star.common.security.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class Rest401Handler implements AuthenticationEntryPoint {

    private static final String UNAUTHORIZED_RESPONSE_BODY =
            """
                {
                    "isError" : true,
                    "message" : "인증이 필요합니다."
                }
            """;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(UNAUTHORIZED_RESPONSE_BODY);
    }
}
