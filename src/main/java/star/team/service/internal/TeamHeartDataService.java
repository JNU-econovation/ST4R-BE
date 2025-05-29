package star.team.service.internal;

import static star.common.constants.CommonConstants.OPTIMISTIC_ATTEMPT_COUNT;

import jakarta.persistence.OptimisticLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.service.BaseHeartService;
import star.member.model.entity.Member;
import star.team.model.entity.Team;
import star.team.model.entity.TeamHeart;
import star.team.repository.TeamHeartRepository;

@Service
public class TeamHeartDataService extends BaseHeartService<TeamHeart, Team> {

    private final TeamHeartRepository teamHeartRepository;

    public TeamHeartDataService(TeamHeartRepository teamHeartRepository) {
        super(teamHeartRepository);
        this.teamHeartRepository = teamHeartRepository;
    }

    @Override
    protected boolean existsByMemberIdAndTargetId(Long memberId, Long teamId) {
        return teamHeartRepository.existsByMemberIdAndTeamId(memberId, teamId);
    }

    @Override
    protected void deleteByMemberIdAndTargetId(Long memberId, Long teamId) {
        teamHeartRepository.deleteHeartByMemberIdAndTeamId(memberId, teamId);
    }

    @Override
    protected TeamHeart createHeartEntity(Member member, Team team) {
        return TeamHeart.builder().member(member).team(team).build();
    }

    @Override
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = OPTIMISTIC_ATTEMPT_COUNT, backoff = @Backoff(delay = 100))
    protected void increaseHeartCount(Team team) {
        team.increaseHeartCount();
        entityManager.flush();
    }

    @Override
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = OPTIMISTIC_ATTEMPT_COUNT, backoff = @Backoff(delay = 100))
    protected void decreaseHeartCount(Team team) {
        team.decreaseHeartCount();
        entityManager.flush();
    }

    @Transactional
    public void deleteHeartsByTeamDelete(Long teamId) {
        teamHeartRepository.deleteHeartsByTeamId(teamId);
    }
}

