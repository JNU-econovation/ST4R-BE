package star.common.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import star.common.security.dto.StarUserDetails;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String BEARER_TYPE = "Bearer ";
    private final static String CRITICAL_AUTH_ERROR_MESSAGE = "알 수 없는 예외로 인한 인증 실패";

    private final JwtManager jwtManager;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final MemberService memberService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return requestIsMatch(request);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) {
        try {
            String path = request.getRequestURI();
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean isHomeBoardPath = pathMatcher.match("/home/board/**", path);

            if (authHeader == null || !authHeader.startsWith(BEARER_TYPE)) {
                if (isHomeBoardPath) {
                    filterChain.doFilter(request, response);
                    return;
                }
                throw new InsufficientAuthenticationException("Auth 헤더가 유효하지 않습니다.");
            }

            String token = authHeader.substring(BEARER_TYPE.length());
            if (!jwtManager.validateToken(token)) {
                throw new BadCredentialsException("토큰이 올바르지 않습니다.");
            }

            Long memberId = jwtManager.extractId(token);
            MemberInfoDTO memberInfo = memberService.getMemberById(memberId);

            StarUserDetails userDetails = new StarUserDetails(memberInfo);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            throw ex;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();

            log.error(CRITICAL_AUTH_ERROR_MESSAGE, ex);
            throw new AuthenticationServiceException(CRITICAL_AUTH_ERROR_MESSAGE, ex);
        }
    }

    private Boolean requestIsMatch(HttpServletRequest request) {
        String path = request.getRequestURI();

        return (HttpMethod.GET.matches(request.getMethod()) && !pathMatcher.match("/home/board/**",
                path))
                || pathMatcher.match("/h2-console/**", path);
    }
}