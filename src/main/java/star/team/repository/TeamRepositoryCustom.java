package star.team.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import star.team.dto.TeamSearchDTO;
import star.team.model.entity.Team;

public interface TeamRepositoryCustom {
    Page<Team> searchTeams(TeamSearchDTO searchDTO, Pageable pageable);
}