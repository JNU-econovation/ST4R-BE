package star.team.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import star.team.model.entity.Team;
import star.team.model.entity.TeamHeart;

public interface TeamHeartRepository extends JpaRepository<TeamHeart, Long>{
    void deleteHeartsByTeamId(Long teamId);

    boolean existsByMemberIdAndTeamId(Long memberId, Long teamId);

    void deleteHeartByMemberIdAndTeamId(Long memberId, Long teamId);

    @Query("SELECT tm.team FROM TeamHeart tm WHERE tm.member.id = :memberId")
    Page<Team> findTeamsByMemberId(Long memberId, Pageable pageable);
}
