package star.common.auth.kakao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoAuthConfig(
        String restApiKey,
        String redirectUri,
        String tokenUrl,
        String userInfoUrl,
        String unlinkUrl
) {
}