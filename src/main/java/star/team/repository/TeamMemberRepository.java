package star.team.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.team.model.entity.TeamMember;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    TeamMember getByTeamIdAndMemberId(Long teamId, Long memberId);

    List<TeamMember> getByTeamId(Long teamId);

    void deleteTeamMembersByTeamId(Long teamId);

    void deleteTeamMembersByTeamIdAndMemberId(Long teamId, Long memberId);

    boolean existsByTeamIdAndMemberId(Long teamId, Long memberId);
}