package star.common.auth.kakao.service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import star.common.auth.exception.InvalidRedirectUriException;
import star.common.auth.kakao.config.KakaoAuthConfig;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.common.exception.InternalServerException;
import star.member.dto.MemberInfoDTO;
import star.member.dto.SocialRegisterDTO;
import star.member.exception.AlreadyInvalidatedTokenException;
import star.member.exception.LoginFailedException;
import star.member.service.MemberService;


@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {

    private final KakaoClientService kakaoClientService;
    private final MemberService memberService;
    private final KakaoAuthConfig kakaoAuthConfig;

    public String getAuthorizationUri(String feRedirectUri) {
        if (!kakaoAuthConfig.allowedFeRedirectOrigins().contains(feRedirectUri))
            throw new InvalidRedirectUriException("%s 는 허용된 redirect uri가 아닙니다.".formatted(feRedirectUri));

        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoAuthConfig.restApiKey())
                .queryParam("redirect_uri", kakaoAuthConfig.beCallbackUrl())
                .queryParam("state", URLEncoder.encode(feRedirectUri, StandardCharsets.UTF_8))
                .build()
                .toUriString();
    }

    public String getHomeUriWithToken(String feRedirectUri, String accessToken) {
        if (!kakaoAuthConfig.allowedFeRedirectOrigins().contains(feRedirectUri))
            throw new InvalidRedirectUriException("%s 는 허용된 redirect uri가 아닙니다.".formatted(feRedirectUri));

        return UriComponentsBuilder
                .fromUriString(feRedirectUri + "/home")
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();
    }

    public MemberInfoDTO kakaoLoginOrRegister(String authorizationCode) {
        String accessToken = kakaoClientService.getAccessToken(authorizationCode);
        KakaoMemberInfoDTO kakaoMemberInfoDTO = kakaoClientService.getMemberInfo(accessToken);

        try {
            // -> LoginFailedException 을 던질 수 있음
            MemberInfoDTO memberInfoDTO = memberService.login(kakaoMemberInfoDTO);

            memberService.setMemberAccessToken(memberInfoDTO.id(), accessToken);
            return memberInfoDTO;
        } catch (LoginFailedException e) { // 유저를 못 찾거나 탈퇴한 유저라면 회원가입
            SocialRegisterDTO registerDTO = SocialRegisterDTO.builder()
                    .SocialAccessToken(accessToken)
                    .email(kakaoMemberInfoDTO.email())
                    .build();
            MemberInfoDTO memberInfoDTO = memberService.register(registerDTO);

            memberService.setMemberAccessToken(memberInfoDTO.id(), accessToken);
            return memberInfoDTO;
        }
    }

    public void kakaoLogout(MemberInfoDTO memberInfoDTO) {
        String kakaoAccessToken = invalidateKakaoAccessToken(memberInfoDTO);
        kakaoClientService.logout(kakaoAccessToken);
    }

    private String invalidateKakaoAccessToken(MemberInfoDTO memberInfoDTO) {
        try {
            return memberService.invalidateAccessToken(memberInfoDTO.id());
        } catch (AlreadyInvalidatedTokenException e) {
            log.error(e.getMessage(), e);
            throw new InternalServerException(e.getMessage());
        }
    }
}
