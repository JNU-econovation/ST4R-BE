package star.common.auth.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import star.common.auth.annotation.AllowedOrigin;
import star.common.auth.kakao.dto.KakaoMemberInfoDTO;
import star.common.auth.kakao.service.KakaoAuthService;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.LoginOrRegisterReportDTO;
import star.member.dto.MemberInfoDTO;
import star.member.dto.SocialRegisterDTO;
import star.member.service.MemberService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCoordinateService {

    private final KakaoAuthService kakaoService;
    private final MemberService memberService;
    private final JwtManager jwtManager;

    @AllowedOrigin(origin = "#feRedirectUri")
    public String getAuthorizationUri(String feRedirectUri) {
        return kakaoService.getAuthorizationUri(feRedirectUri);
    }

    @AllowedOrigin(origin = "#origin")
    @Transactional
    public String getRedirectUriWithToken(String origin, String authorizationCode) {
        String kakaoAccessToken = kakaoService.getAccessToken(authorizationCode);
        KakaoMemberInfoDTO kakaoMemberInfoDTO = kakaoService.getMemberInfo(kakaoAccessToken);

        LoginOrRegisterReportDTO result = loginOrRegister(kakaoMemberInfoDTO, kakaoAccessToken);

        memberService.updateAccessToken(result.memberInfoDTO().id(), kakaoAccessToken);

        String path = result.isRegister() ? "/register" : "/home";

        return UriComponentsBuilder
                .fromUriString(origin + path)
                .queryParam("accessToken", jwtManager.generateToken(result.memberInfoDTO()))
                .build()
                .toUriString();
    }

    @Transactional
    public void logout(MemberInfoDTO memberInfoDTO) {
        String kakaoAccessToken = memberService.invalidateAccessToken(memberInfoDTO.id());
        kakaoService.logout(kakaoAccessToken);
    }

    private LoginOrRegisterReportDTO loginOrRegister(KakaoMemberInfoDTO kakaoMemberInfoDTO,
            String kakaoAccessToken) {

        Optional<MemberInfoDTO> optionalMemberInfo = memberService.login(kakaoMemberInfoDTO);

        if (optionalMemberInfo.isEmpty()) {
            SocialRegisterDTO registerDTO = SocialRegisterDTO.builder()
                    .socialAccessToken(kakaoAccessToken)
                    .email(kakaoMemberInfoDTO.email())
                    .build();

            return LoginOrRegisterReportDTO.builder()
                    .isRegister(true)
                    .memberInfoDTO(memberService.processRegistration(registerDTO))
                    .build();
        }

        return LoginOrRegisterReportDTO.builder()
                .isRegister(false)
                .memberInfoDTO(optionalMemberInfo.get())
                .build();
    }
}
