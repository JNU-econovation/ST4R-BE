package star.team.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import star.team.model.entity.Team;
import star.team.model.entity.TeamMember;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> getByTeamIdAndMemberId(Long teamId, Long memberId);

    List<TeamMember> getByTeamId(Long teamId);

    void deleteTeamMembersByTeamId(Long teamId);


    boolean existsByTeamIdAndMemberId(Long teamId, Long memberId);

    List<TeamMember> getByTeamIdAndIsBanned(Long teamId, boolean b);

    @Query("SELECT tm.member.id FROM TeamMember tm WHERE tm.team.id = :teamId")
    List<Long> getAllMemberIdInTeam(Long teamId);

    @Query("SELECT tm.team.id FROM TeamMember tm WHERE tm.member.id = :memberId AND tm.isDeprecated = false AND tm.isBanned = false")
    List<Long> getTeamIdsByMemberId(Long memberId);

    @Query("SELECT tm.team FROM TeamMember tm WHERE tm.member.id = :memberId AND tm.isDeprecated = false AND tm.isBanned = false")
    List<Team> getTeamsByMemberId(Long memberId);
}