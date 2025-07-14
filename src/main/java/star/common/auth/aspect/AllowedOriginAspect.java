package star.common.auth.aspect;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import star.common.auth.annotation.AllowedOrigin;
import star.common.auth.config.AuthProperties;
import star.common.auth.exception.InvalidRedirectUriException;
import star.common.resolver.AspectParameterResolver;

@Aspect
@Component
@RequiredArgsConstructor
public class AllowedOriginAspect {

    private final AspectParameterResolver resolver;
    private final AuthProperties authProperties;

    @Before("@annotation(allowedOrigin)")
    public void checkTeamLeader(JoinPoint joinPoint, AllowedOrigin allowedOrigin) {
        EvaluationContext context = resolver.buildEvaluationContext(joinPoint);

        String origin = resolver.resolve(allowedOrigin.origin(), context, String.class);

        if (!authProperties.getAllowedFeRedirectOrigins().contains(origin)) {
            throw new InvalidRedirectUriException(origin);
        }
    }
}
