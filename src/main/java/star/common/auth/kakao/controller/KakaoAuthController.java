package star.common.auth.kakao.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import star.common.auth.service.AuthCoordinateService;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;

@Controller
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final AuthCoordinateService service;

    @GetMapping
    public String startKakaoOauth(@RequestParam("redirect") String feRedirectUri) {
        return "redirect:" + service.getAuthorizationUri(feRedirectUri);
    }

    @GetMapping("/callback")
    public String loginOrRegister(@RequestParam String code, @RequestParam String state) {
        return "redirect:" + service.getRedirectUriWithToken(state, code);
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        service.logout(userDetails.getMemberInfoDTO());

        return ResponseEntity.ok(CommonResponse.success());
    }
}
