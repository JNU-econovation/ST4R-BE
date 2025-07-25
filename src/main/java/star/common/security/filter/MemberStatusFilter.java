package star.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import star.common.security.dto.StarUserDetails;
import star.common.security.exception.AlreadyWithdrawMemberException;
import star.common.security.exception.RegisterNotCompletedException;
import star.common.security.exception.handler.rest.Rest403Handler;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberStatusFilter extends OncePerRequestFilter {

    private final Rest403Handler rest403Handler;

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

        if (HttpMethod.GET.matches(method) && uri.startsWith("/upload/s3/presigned-url")) {
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

        switch (principal.getStatus()) {

            case REGISTERING -> {
                log.warn("회원가입 미완료 사용자의 접근 시도 차단. MemberInfo: {}, URI: {}",
                        principal.getMemberInfoDTO(),
                        request.getRequestURI()
                );

                rest403Handler.handle(request, response, new RegisterNotCompletedException());
                return;
            }

            case INACTIVATED -> {
                log.warn("비활성화된 사용자의 접근 시도 차단. MemberInfo: {}, URI: {}",
                        principal.getMemberInfoDTO(),
                        request.getRequestURI()
                );

                rest403Handler.handle(request, response, new AlreadyWithdrawMemberException());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
