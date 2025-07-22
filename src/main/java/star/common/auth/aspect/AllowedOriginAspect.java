package star.common.auth.aspect;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import star.common.auth.annotation.AllowedOrigin;
import star.common.security.config.AllowedOriginsProperties;
import star.common.auth.exception.InvalidRedirectUriException;
import star.common.resolver.AspectParameterResolver;

@Aspect
@Component
@RequiredArgsConstructor
public class AllowedOriginAspect {

    private final AspectParameterResolver resolver;
    private final AllowedOriginsProperties allowedOriginsProperties;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Before("@annotation(allowedOrigin)")
    public void checkTeamLeader(JoinPoint joinPoint, AllowedOrigin allowedOrigin) {
        EvaluationContext context = resolver.buildEvaluationContext(joinPoint);
        String origin = resolver.resolve(allowedOrigin.origin(), context, String.class);

        boolean matched = allowedOriginsProperties.getAllowedFeRedirectOrigins().stream()
                .anyMatch(pattern -> matcher.match(pattern, origin));

        if (!matched) {
            throw new InvalidRedirectUriException(origin);
        }
    }
}
