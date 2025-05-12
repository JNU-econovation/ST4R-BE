package star.common.auth.kakao.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import star.common.auth.kakao.service.KakaoAuthService;
import star.common.security.jwt.JwtManager;

@RestController
@RequestMapping("/oauth/kakao/callback")
@RequiredArgsConstructor
public class KakaoAuthRestController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtManager jwtManager;


    @GetMapping
    public String loginOrRegister(@RequestParam(name = "code") String code) {
        return jwtManager.generateToken(kakaoAuthService.loginOrRegister(code));
    }
}
