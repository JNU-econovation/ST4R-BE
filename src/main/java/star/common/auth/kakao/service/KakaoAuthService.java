package star.common.auth.kakao.service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import star.common.auth.kakao.config.KakaoAuthConfig;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.common.auth.kakao.dto.KakaoMemberWithdrawDTO;


@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {

    private final KakaoClientService kakaoClientService;
    private final KakaoAuthConfig kakaoAuthConfig;

    public String getAuthorizationUri(String feRedirectUri) {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoAuthConfig.restApiKey())
                .queryParam("redirect_uri", kakaoAuthConfig.beCallbackUrl())
                .queryParam("state", URLEncoder.encode(feRedirectUri, StandardCharsets.UTF_8))
                .build()
                .toUriString();
    }

    public String getAccessToken(String authorizationCode) {
        return kakaoClientService.getAccessToken(authorizationCode);
    }

    public KakaoMemberInfoDTO getMemberInfo(String accessToken) {
        return kakaoClientService.getMemberInfo(accessToken);
    }

    public void logout(String kakaoAccessToken) {
        kakaoClientService.logout(kakaoAccessToken);
    }

    public void unlink(KakaoMemberWithdrawDTO withdrawDTO) {
        kakaoClientService.unlinkKakao(withdrawDTO);
    }


}
