package star.team.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import star.team.model.entity.TeamImage;

@Repository
public interface TeamImageRepository extends CrudRepository<TeamImage, Long> {

    void deleteTeamImageByTeamId(Long id);

    void deleteTeamImagesByTeamId(Long teamId);
}
