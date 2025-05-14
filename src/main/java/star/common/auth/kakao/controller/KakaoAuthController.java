package star.common.auth.kakao.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import star.common.auth.kakao.service.KakaoAuthService;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.MemberInfoDTO;

@Controller
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtManager jwtManager;

    @Value("${fe.dev.redirect-uri}")
    private String feRedirectUri;

    @GetMapping("/callback")
    public String loginOrRegister(@RequestParam String code) {
        String accessToken = jwtManager.generateToken(kakaoAuthService.kakaoLoginOrRegister(code));

        String redirectUrl = UriComponentsBuilder
                .fromUriString(feRedirectUri + "/home")
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        return "redirect:" + redirectUrl;
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@AuthenticationPrincipal StarUserDetails userDetails) {
        MemberInfoDTO memberInfoDTO = userDetails.getMemberInfoDTO();
        kakaoAuthService.kakaoLogout(memberInfoDTO);
        return new ResponseEntity<>(CommonResponse.success(), HttpStatus.OK);
    }
}
