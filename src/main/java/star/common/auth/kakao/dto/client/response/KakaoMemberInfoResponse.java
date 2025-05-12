package star.common.auth.kakao.dto.client.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoMemberInfoResponse(
        Long id,
        Date connectedAt,
        KakaoAccount kakaoAccount
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
            Boolean profileNicknameNeedsAgreement,

            Boolean hasEmail,

            String email,

            Profile profile
    ) {

        public record Profile(String nickname) {

        }
    }
}
