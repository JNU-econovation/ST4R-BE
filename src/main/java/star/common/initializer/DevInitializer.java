package star.common.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;

@Profile("dev")
@Component
@Slf4j
@RequiredArgsConstructor
public class DevInitializer {

    private final MemberService memberService;
    private final JwtManager jwtManager;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        MemberInfoDTO admin = memberService.getMemberById(1L);
        MemberInfoDTO member = memberService.getMemberById(2L);

        String adminToken = jwtManager.generateToken(admin);
        String memberToken = jwtManager.generateToken(member);

        log.info("관리자 토큰: {}", adminToken);
        log.info("일반 유저 토큰: {}", memberToken);
    }
}
