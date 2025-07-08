package star.team.chat.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.team.chat.model.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> getChatsByTeamMemberTeamId(Long teamId, Pageable pageable);

    void deleteChatsByTeamMemberTeamId(Long teamId);

    Optional<Chat> getChatById(Long id);

    boolean existsByIdAndChattedAtBefore(Long id, LocalDateTime readTime);

    long countByTeamMemberTeamIdAndChattedAtAfter(Long teamId, LocalDateTime lastReadAt);

    long countByTeamMemberTeamIdAndChattedAtAfterAndRedisIdNotIn(Long teamId,
            LocalDateTime lastReadAt, Set<Long> redisIds);
}
