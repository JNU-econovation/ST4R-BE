package star.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.team.model.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

}
