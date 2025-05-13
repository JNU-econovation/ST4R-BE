package star.common.auth.kakao.dto.client.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenResponse(
        @NotNull
        String accessToken,

        @NotNull
        String tokenType,

        @NotNull
        String refreshToken,

        @NotNull
        String idToken,

        @NotNull
        Integer expiresIn,

        @NotNull
        Integer refreshTokenExpiresIn,

        @NotNull
        String scope
) {

}
