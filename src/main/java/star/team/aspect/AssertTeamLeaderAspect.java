package star.team.aspect;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import star.common.resolver.AspectParameterResolver;
import star.member.dto.MemberInfoDTO;
import star.team.annotation.AssertTeamLeader;
import star.team.exception.YouAreNotTeamLeaderException;
import star.team.model.entity.Team;
import star.team.service.internal.TeamDataService;

@Aspect
@Component
@RequiredArgsConstructor
public class AssertTeamLeaderAspect {

    private final TeamDataService teamDataService;
    private final AspectParameterResolver resolver;

    @Before("@annotation(assertTeamLeader)")
    public void checkTeamLeader(JoinPoint joinPoint, AssertTeamLeader assertTeamLeader) {
        EvaluationContext context = resolver.buildEvaluationContext(joinPoint);

        Long teamId = resolver.resolve(assertTeamLeader.teamId(), context, Long.class);
        MemberInfoDTO memberInfo = resolver.resolve(assertTeamLeader.memberInfo(), context,
                MemberInfoDTO.class);

        Team team = teamDataService.getTeamEntityById(teamId);
        if (memberInfo == null || !team.getLeader().getId().equals(memberInfo.id())) {
            throw new YouAreNotTeamLeaderException();
        }
    }
}

