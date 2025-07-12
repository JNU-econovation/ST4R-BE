package star.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import star.team.model.entity.TeamHeart;

public interface TeamHeartRepository extends JpaRepository<TeamHeart, Long>{
    void deleteHeartsByTeamId(Long teamId);

    boolean existsByMemberIdAndTeamId(Long memberId, Long teamId);

    void deleteHeartByMemberIdAndTeamId(Long memberId, Long teamId);
}
