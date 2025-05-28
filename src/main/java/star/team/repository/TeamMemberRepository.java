package star.team.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import star.team.model.entity.TeamMember;

@Repository
public interface TeamMemberRepository extends CrudRepository<TeamMember, Long> {

    void deleteTeamMembersByTeamId(Long teamId);
}
