package star.team.chat.service.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import star.team.chat.dto.response.ChatResponse;

@Service
@RequiredArgsConstructor
public class RedisChatPublisher {

    private final RedisTemplate<String, ChatResponse> chatRedisTemplate;

    public void publishChat(ChannelTopic topic, ChatResponse chatResponse) {
        chatRedisTemplate.convertAndSend(topic.getTopic(), chatResponse);
    }
}
