package star.common.auth.kakao.client;

import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import star.common.auth.kakao.config.KakaoAuthConfig;
import star.common.auth.kakao.dto.KakaoMemberWithdrawDTO;
import star.common.auth.kakao.dto.client.response.KakaoMemberInfoResponse;
import star.common.auth.kakao.dto.client.response.TokenResponse;

@Component
public class KakaoApiClient {

    private final KakaoAuthConfig kakaoAuthConfig;
    private final RestClient client;

    public KakaoApiClient(KakaoAuthConfig kakaoAuthConfig) {
        this.kakaoAuthConfig = kakaoAuthConfig;
        this.client = RestClient.builder().build();
    }

    public TokenResponse getToken(String authorizationCode) {
        var body = makeBody(kakaoAuthConfig.restApiKey(), authorizationCode);
        return client.post()
                .uri(URI.create(kakaoAuthConfig.tokenUrl()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(TokenResponse.class)
                .getBody();
    }

    public KakaoMemberInfoResponse getMemberInfo(String kakaoAccessToken) {
        return client.get()
                .uri(URI.create(kakaoAuthConfig.userInfoUrl()))
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .header("Content-type",
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8")
                .retrieve()
                .toEntity(KakaoMemberInfoResponse.class)
                .getBody();
    }

    public void logout(String kakaoAccessToken) {
        client.post()
                .uri(URI.create(kakaoAuthConfig.userInfoUrl()))
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .header("Content-type",
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8")
                .retrieve();
    }


    public void unlinkKakao(KakaoMemberWithdrawDTO kakaoMemberWithdrawDTO) {
        client.post()
                .uri(URI.create(kakaoAuthConfig.unlinkUrl()))
                .header("Authorization", "Bearer " + kakaoMemberWithdrawDTO.kakaoAccessToken())
                .header("Content-type",
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset_UTF-8")
                .retrieve();
    }

    private LinkedMultiValueMap<String, String> makeBody(String clientId, String code) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("code", code);
        return body;
    }
}