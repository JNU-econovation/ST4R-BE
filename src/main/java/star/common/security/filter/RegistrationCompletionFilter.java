package star.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import star.common.security.dto.StarUserDetails;
import star.common.security.exception.handler.Rest403Handler;
import star.member.dto.MemberInfoDTO;
import star.member.model.vo.MemberStatus;
import star.member.service.MemberService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompletionFilter extends OncePerRequestFilter {

    private final Rest403Handler rest403Handler;
    private final MemberService memberService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (HttpMethod.PATCH.matches(method) && uri.equals("/members/completeRegistration")) {
            return true;
        }

        if (HttpMethod.GET.matches(method) && uri.startsWith("/members/exists")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        StarUserDetails principal = (StarUserDetails) authentication.getPrincipal();
        MemberInfoDTO memberInfo = principal.getMemberInfoDTO();

        MemberStatus status = memberService.getMemberStatusById(memberInfo.id());

        if (status.equals(MemberStatus.REGISTERING)) {
            log.warn("회원가입 미완료 사용자의 접근 시도 차단. MemberInfo: {}, URI: {}", memberInfo,
                    request.getRequestURI());

            AccessDeniedException ex = new AccessDeniedException("회원가입이 완료되지 않은 사용자 입니다.");
            rest403Handler.handle(request, response, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
