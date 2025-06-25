package star.common.security.filter;


import static star.common.security.constants.SecurityConstants.BEARER_TYPE;
import static star.common.security.constants.SecurityConstants.CRITICAL_AUTH_ERROR_MESSAGE;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import star.common.security.exception.handler.Rest401Handler;
import star.common.security.exception.handler.Rest500Handler;
import star.common.security.helper.JwtAuthHelper;


@Component
@RequiredArgsConstructor
@Slf4j
public class RestJwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> WHITELIST_PATHS = List.of("/h2-console/**", "/oauth/**", "/websocket/**");
    private static final List<String> BLACKLIST_PATHS = List.of("/upload/**");
    private static final List<String> GREYLIST_PATHS = List.of("/home/**", "/groups/**");


    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtAuthHelper jwtAuthHelper;
    private final Rest401Handler rest401Handler;
    private final Rest500Handler rest500Handler;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 화이트리스트 검사 (람다식 및 스트림 API 사용)
        boolean isWhitelisted = WHITELIST_PATHS.stream()
                .anyMatch(whitePattern -> pathMatcher.match(whitePattern, path));

        boolean isBlacklisted = BLACKLIST_PATHS.stream()
                .anyMatch(blackPattern -> pathMatcher.match(blackPattern, path));

        if (isWhitelisted) {
            return true;
        }

        if (isBlacklisted) {
            return false;
        }

        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException {
        try {
            String path = request.getRequestURI();
            String method = request.getMethod();
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            boolean isGreylisted = GREYLIST_PATHS.stream()
                    .anyMatch(greyPattern -> pathMatcher.match(greyPattern, path));

            if (authHeader == null || !authHeader.startsWith(BEARER_TYPE)) {

                if (isGreylisted && method.equals(HttpMethod.GET.name())) {
                    filterChain.doFilter(request, response);
                    return;
                }

                throw new InsufficientAuthenticationException("Auth 헤더가 유효하지 않습니다.");
            }

            UsernamePasswordAuthenticationToken auth =
                    (UsernamePasswordAuthenticationToken) jwtAuthHelper.authenticate(authHeader);

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            rest401Handler.commence(request, response, ex);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();

            log.error(CRITICAL_AUTH_ERROR_MESSAGE, ex);
            rest500Handler.commence(request, response,
                    new AuthenticationServiceException(CRITICAL_AUTH_ERROR_MESSAGE, ex));

        }
    }

}