package star.team.aspect;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import star.common.resolver.AspectParameterResolver;
import star.member.dto.MemberInfoDTO;
import star.team.annotation.AssertTeamMember;
import star.team.exception.TeamMemberNotFoundException;
import star.team.service.internal.TeamMemberDataService;

@Aspect
@Component
@RequiredArgsConstructor
public class AssertTeamMemberAspect {

    private final TeamMemberDataService teamMemberDataService;
    private final AspectParameterResolver resolver;

    @Before("@annotation(assertTeamMember)")
    public void checkTeamMember(JoinPoint joinPoint, AssertTeamMember assertTeamMember) {
        EvaluationContext context = resolver.buildEvaluationContext(joinPoint);

        Long teamId = resolver.resolve(assertTeamMember.teamId(), context, Long.class);
        MemberInfoDTO memberInfo = resolver.resolve(assertTeamMember.memberInfo(), context,
                MemberInfoDTO.class);

        if (memberInfo == null || !teamMemberDataService.existsTeamMember(teamId,
                memberInfo.id())) {
            throw new TeamMemberNotFoundException();
        }
    }
}
