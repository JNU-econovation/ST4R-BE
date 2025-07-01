package star.team.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.team.model.entity.TeamImage;

@Repository
public interface TeamImageRepository extends JpaRepository<TeamImage, Long> {

    void deleteTeamImageByTeamId(Long id);

    void deleteTeamImagesByTeamId(Long teamId);

    List<TeamImage> getTeamImagesByTeamId(Long teamId);
}