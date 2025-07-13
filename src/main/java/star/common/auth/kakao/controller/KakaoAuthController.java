package star.common.auth.kakao.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import star.common.auth.kakao.service.KakaoAuthService;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.common.security.encryption.jwt.JwtManager;

@Controller
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtManager jwtManager;

    @GetMapping
    public String startKakaoOauth(@RequestParam("redirect") String feRedirectUri) {
        return "redirect:" + kakaoAuthService.getAuthorizationUri(feRedirectUri);
    }

    @GetMapping("/callback")
    public String loginOrRegister(@RequestParam String code, @RequestParam String state) {
        String accessToken = jwtManager.generateToken(kakaoAuthService.kakaoLoginOrRegister(code));
        return "redirect:" + kakaoAuthService.getHomeUriWithToken(state, accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        kakaoAuthService.kakaoLogout(userDetails.getMemberInfoDTO());

        return ResponseEntity.ok(CommonResponse.success());
    }
}
