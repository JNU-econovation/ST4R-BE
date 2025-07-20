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

    List<TeamMember> getByTeamId(Long teamId);

    void deleteTeamMembersByTeamId(Long teamId);


    Optional<TeamMember> getOptionalTeamMemberByTeamIdAndMemberId(Long teamId, Long memberId);

    @Query("""
                SELECT
                    EXISTS(
                        SELECT 1 FROM TeamMember tm
                        WHERE tm.team.id = :teamId
                        AND tm.member.id = :memberId
                        AND tm.isDeprecated = :isDeprecated
                        AND tm.isBanned = :isBanned
                    )
            """)
    boolean existsTeamMember(
            Long teamId, Long memberId, boolean isBanned, boolean isDeprecated
    );

    @Query("""
                SELECT
                    EXISTS(
                        SELECT 1 FROM TeamMember tm
                        WHERE tm.team.id = :teamId
                        AND tm.member.id = :memberId
                        AND tm.isBanned = :isBanned
                    )
            """)
    boolean existsTeamMember(Long teamId, Long memberId, boolean isBanned);

    List<TeamMember> getByTeamIdAndIsBanned(Long teamId, boolean b);

    @Query("SELECT tm.member.id FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.isDeprecated = false AND tm.isBanned = false")
    List<Long> getAllMemberIdInTeam(Long teamId);

    @Query("SELECT tm.team.id FROM TeamMember tm WHERE tm.member.id = :memberId AND tm.isDeprecated = false AND tm.isBanned = false")
    List<Long> getTeamIdsByMemberId(Long memberId);

    @Query("SELECT tm.team FROM TeamMember tm WHERE tm.member.id = :memberId AND tm.isDeprecated = false AND tm.isBanned = false")
    List<Team> getTeamsByMemberId(Long memberId);
}