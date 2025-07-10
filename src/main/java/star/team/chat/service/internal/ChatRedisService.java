package star.team.chat.service.internal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRedisService {
    private final RedisTemplate<String, LocalDateTime> localDateTimeRedisTemplate;

    public void markAsRead(Long teamId, Long memberId, LocalDateTime readTime) {
        String key = buildReadKey(teamId, memberId);

        localDateTimeRedisTemplate.opsForValue().set(key, readTime);
    }

    public LocalDateTime getLastReadTime(Long teamId, Long memberId) {
        String key = buildReadKey(teamId, memberId);
        LocalDateTime value = localDateTimeRedisTemplate.opsForValue().get(key);

        return value != null ? value : LocalDateTime.MIN;
    }


    public void deleteAllByTeamId(Long teamId) {
        String pattern = "read:chat:" + teamId + ":*";
        List<String> keysToDelete = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern).count(1000).build();

        try (Cursor<String> cursor = localDateTimeRedisTemplate.scan(scanOptions)) {
            cursor.forEachRemaining(keysToDelete::add);
        }

        if (!keysToDelete.isEmpty()) {
            localDateTimeRedisTemplate.delete(keysToDelete);
        }
    }


    private String buildReadKey(Long teamId, Long memberId) {
        return "read:chat:" + teamId + ":" + memberId;
    }


    private String buildChatKey(Long teamId) {
        return "chat:room:" + teamId;
    }
}
