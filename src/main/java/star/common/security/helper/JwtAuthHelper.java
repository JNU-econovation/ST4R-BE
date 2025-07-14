package star.common.security.helper;

import static star.common.security.constants.SecurityConstants.BEARER_TYPE;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import star.common.security.dto.StarUserDetails;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;

@Component
@RequiredArgsConstructor
public class JwtAuthHelper {

    private final JwtManager jwtManager;
    private final MemberService memberService;

    public Authentication authenticate(String fullAuthHeader) {
        if (!fullAuthHeader.startsWith(BEARER_TYPE)) {
            throw new BadCredentialsException("인증 헤더가 'Bearer' 토큰 형식이 아닙니다.");
        }

        String token = fullAuthHeader.substring(BEARER_TYPE.length());

        if (!jwtManager.validateToken(token)) {
            throw new BadCredentialsException("JWT 토큰이 올바르지 않거나 만료되었습니다.");
        }

        Long memberId = jwtManager.extractId(token);
        MemberInfoDTO memberInfo = memberService.getMemberById(memberId);

        StarUserDetails userDetails = new StarUserDetails(memberInfo);

        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }
}