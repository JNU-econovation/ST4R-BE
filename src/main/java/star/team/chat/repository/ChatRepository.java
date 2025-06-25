package star.team.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.team.chat.model.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> getChatsByTeamMemberTeamId(Long teamId, Pageable pageable);
}
