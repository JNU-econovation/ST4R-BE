package star.e2e.dto.request;

import io.restassured.http.Method;
import lombok.Builder;

@Builder
public record E2ERequest(
        Method method,
        String url,
        String accessToken,
        Object body,
        Integer expectedStatusCode
) {

}
