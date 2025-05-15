package star.common.auth.kakao.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoAuthConfig(
        String restApiKey,
        String tokenUrl,
        String userInfoUrl,
        String unlinkUrl,
        String logoutUrl,
        String beCallbackUrl,
        List<String> allowedFeRedirectOrigins
) {
}