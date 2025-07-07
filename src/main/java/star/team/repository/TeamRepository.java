package star.team.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import star.team.model.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {

    @Query("SELECT t.id FROM Team t")
    List<Long> getAllTeamIds();

}
