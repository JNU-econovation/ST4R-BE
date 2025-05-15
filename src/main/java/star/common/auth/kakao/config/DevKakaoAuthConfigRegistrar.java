package star.common.auth.kakao.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")
@EnableConfigurationProperties(KakaoAuthConfig.class)  // KakaoAuthConfig를 빈으로 등록
@PropertySource({"classpath:application-kakao-auth.properties",
        "classpath:application-kakao-auth-dev.properties", "classpath:application-secret.properties"})
public class DevKakaoAuthConfigRegistrar { }