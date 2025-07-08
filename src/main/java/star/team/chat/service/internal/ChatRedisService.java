package star.team.chat.service.internal;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import star.team.chat.config.ChatRedisProperties;
import star.team.chat.dto.ChatDTO;
import star.team.chat.exception.RedisRangeExceededException;

@Service
@RequiredArgsConstructor
public class ChatRedisService {

    private final RedisTemplate<String, ChatDTO> chatDTORedisTemplate;
    private final RedisTemplate<String, LocalDateTime> localDateTimeRedisTemplate;
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final ChatRedisProperties chatRedisProperties;

    public void markAsRead(Long teamId, Long memberId, LocalDateTime readTime) {
        String key = buildReadKey(teamId, memberId);
        //opsForValue : 단순 키 - 값 형태
        localDateTimeRedisTemplate.opsForValue().set(key, readTime);
    }

    //마지막으로 읽은 time 조회
    public LocalDateTime getLastReadTime(Long teamId, Long memberId) {
        String key = buildReadKey(teamId, memberId);
        LocalDateTime value = (LocalDateTime) localDateTimeRedisTemplate.opsForValue().get(key);

        return value != null ? value : LocalDateTime.MIN;
    }

    public ChatDTO saveMessage(Long teamId, ChatDTO chat) {

        Long newRedisId = generateMessageId(teamId);
        ChatDTO redisChat = ChatDTO.from(chat, newRedisId);

        String key = buildChatKey(teamId);
        chatDTORedisTemplate.opsForList().rightPush(key, redisChat);
        chatDTORedisTemplate.opsForList().trim(key, -chatRedisProperties.getMaxSize(), -1);

        return redisChat;
    }

    public List<ChatDTO> getChats(Long teamId, Integer page, Integer size, boolean ignoreRangeExceed) {
        String key = buildChatKey(teamId);

        Long sizeInRedis = chatDTORedisTemplate.opsForList().size(key);
        if (sizeInRedis == null || sizeInRedis == 0) {
            return Collections.emptyList();
        }

        int start = page * size;
        int end = start + size - 1;

        if (end >= sizeInRedis) {
            end = sizeInRedis.intValue() - 1;

            if (!ignoreRangeExceed) {
                throw new RedisRangeExceededException(
                        chatDTORedisTemplate.opsForList().range(key, start, end)
                );
            }
        }

        return chatDTORedisTemplate.opsForList().range(key, start, end);
    }

    public Integer countReaders(ChatDTO chat, List<Long> allMemberIds) {
        int count = 0;
        for (Long memberId : allMemberIds) {
            LocalDateTime readTime = getLastReadTime(chat.teamId(), memberId);
            if (readTime.isAfter(chat.chattedAt()) || readTime.isEqual(chat.chattedAt())) {
                count++;
            }
        }
        return count;
    }

    public Long updateChatWithDbIdMap(Map<Long, Map<Long, ChatDTO>> teamRedisChatMap) {
        Long count = 0L;

        for (Map.Entry<Long, Map<Long, ChatDTO>> teamEntry : teamRedisChatMap.entrySet()) {
            Long teamId = teamEntry.getKey();
            Map<Long, ChatDTO> redisIdToUpdatedChatDTO = teamEntry.getValue();


            String redisListKey = buildChatKey(teamId);
            List<ChatDTO> redisChats = chatDTORedisTemplate.opsForList().range(redisListKey, 0, -1);

            if (redisChats == null || redisChats.isEmpty()) {
                continue;
            }

            for (int i = 0; i < redisChats.size(); i++) {
                ChatDTO oldChat = redisChats.get(i);

                Long redisId = oldChat.chatRedisId();
                ChatDTO updatedChat = redisIdToUpdatedChatDTO.get(redisId);

                // chatDbId가 null → 아직 DB에 저장 안됐던 채팅만 업데이트
                if (updatedChat != null && oldChat.chatDbId() == null
                        && updatedChat.chatDbId() != null) {
                    // replace 해당 index
                    chatDTORedisTemplate.opsForList().set(redisListKey, i, updatedChat);
                    count++;
                }
            }
        }
        return count;
    }

    public void deleteAllByTeamId(Long teamId) {
        String chatKey = buildChatKey(teamId);
        chatDTORedisTemplate.delete(chatKey);

        String messageIdKey = "chat:room:" + teamId + ":messageId";
        longRedisTemplate.delete(messageIdKey);

        String pattern = "read:chat:" + teamId + ":*";

        // keys()로 패턴에 맞는 키 모두 조회
        var keys = longRedisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            localDateTimeRedisTemplate.delete(keys);
        }
    }




    private String buildReadKey(Long teamId, Long memberId) {
        return "read:chat:" + teamId + ":" + memberId;
    }


    private String buildChatKey(Long teamId) {
        return "chat:room:" + teamId;
    }

    private Long generateMessageId(Long teamId) {
        String key = "chat:room:" + teamId + ":messageId";
        return longRedisTemplate.opsForValue().increment(key);
    }
}
